package villalobos.diego.x3dpark

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import villalobos.diego.x3dpark.Adapters.Spots
import villalobos.diego.x3dpark.Data.Spot
import villalobos.diego.x3dpark.Data.User

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var user:User
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var spots: ArrayList<Spot>
    private lateinit var nearbySpots: ArrayList<Spot>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var range = 3000
    private val city = "Guadalajara"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nav_view.setNavigationItemSelectedListener(this)

        user = intent.getSerializableExtra("user") as User

        Log.wtf("TOKEN",user.idToken)

        val inflatedView = nav_view.inflateHeaderView(R.layout.nav_header_main)
        val id = inflatedView.findViewById<ImageView>(R.id.nav_image_profile)

        if(user.photo != "null"){
            Glide.with(this)
                    .load(user.photo)
                    .into(id)
        }
        val name = inflatedView.findViewById<TextView>(R.id.nav_text_name)
        name.text = user.name

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.main_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val rangeS = "$range M"
        main_text_range.text = rangeS
    }

    fun toggleDrawer(_v:View){
        drawer_layout.openDrawer(Gravity.START)
    }

    fun openProfile(_v:View){
        val intent = Intent(this,ProfileActivity::class.java)
        intent.putExtra("user",user)
        startActivity(intent)
    }

    private fun setUpMap() {
        if(checkLocationPermissions()){
            mMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location:Location? ->
                        val coordinates = LatLng(location?.latitude!!,location.longitude)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates,14f))
                    }
        }
    }

    fun checkLocationPermissions():Boolean{
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return false
        }
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    fun toggleNearbyDrawer(v:View){
        if(main_linear_drawer.visibility == View.GONE){
            main_linear_drawer.visibility = View.VISIBLE
            val leftSwipe = AnimationUtils.loadAnimation(this,R.anim.layout_open_left)
            main_linear_drawer.startAnimation(leftSwipe)
            getNearbySpots()
        }else{
            main_linear_drawer.visibility = View.GONE
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        getAllSpotsInCity()

        setUpMap()
    }

    fun onSpotPressed(spot:Spot){
        val intent = Intent(this,SpotDetailActivity::class.java)
        intent.putExtra("spot",spot)
        startActivity(intent)
    }

    fun getAllSpotsInCity() {
        val url = getString(R.string.API_URL) + getString(R.string.API_POSTS_GET_ALL_POSTS)

        try {
            url.httpGet().header(Pair("authorization",user.idToken)).responseObject { request: Request, response: Response, result: Result<ArrayList<Spot>, FuelError> ->

                when (response.statusCode){
                    200 -> {
                        spots = result.get()
                        drawCitySpots()
                    }
                    400 -> {

                    }
                    403 -> {
                        startLoginActivity()
                    }
                }
            }
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }

    fun drawCitySpots(){
        for (spot:Spot in spots){

            val marker = LatLng(spot.coordinates._latitude,spot.coordinates._longitude)
            val address = spot.address
            val currentFare = "${spot.fares.current} ${spot.fares.currency} / ${spot.fares.rate}"

            val markerOptions = MarkerOptions()
            markerOptions.position(marker)
            markerOptions.title("${address.street} ${address.number} $currentFare")

            mMap.addMarker(markerOptions)
        }
    }


    fun startLoginActivity(){
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun getNearbySpots(){
        main_recycler_spots.visibility = View.GONE
        main_text_no_nearby.visibility = View.GONE
        main_progress_nearby_spots.visibility = View.VISIBLE
        if(checkLocationPermissions()){
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location:Location? ->
                        val coordinates = LatLng(location?.latitude!!,location.longitude)

                        val url = getString(R.string.API_URL) + getString(R.string.API_POSTS_GET_NEARBY_POSTS)
                        val params = listOf("coordinates" to "${coordinates.latitude},${coordinates.longitude}",
                                "range" to range,
                                "city" to city)
                        url.httpGet(params).header(Pair("authorization",user.idToken)).responseObject { request: Request, response: Response, result: Result<ArrayList<Spot>, FuelError> ->
                            Log.d("SPOTS",result.toString())
                            when(response.statusCode){
                                200 -> {
                                    main_progress_nearby_spots.visibility = View.GONE

                                    nearbySpots = result.get()
                                    if(nearbySpots.isEmpty()){
                                        main_text_no_nearby.visibility = View.VISIBLE
                                    }else{
                                        main_recycler_spots.visibility = View.VISIBLE
                                        drawNearbySpotsRecycler()
                                    }
                                }
                                403 -> {
                                    startLoginActivity()
                                }
                            }
                        }
            }
        }
    }

    fun changeNearbyRange(v: View){
        when (v){
            main_image_minus_range -> {
                range -= 500
            }
            main_image_plus_range -> {
                range += 500
            }
        }
        getNearbySpots()
        val rangeS = "$range M"
        main_text_range.text = rangeS
    }

    fun drawNearbySpotsRecycler() {
        viewManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        viewAdapter = Spots(nearbySpots,{ spot:Spot -> onSpotPressed(spot)})

        recyclerView = findViewById<RecyclerView>(R.id.main_recycler_spots).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                val intent = Intent(this,LoginActivity::class.java)
                                startActivity(intent)
                                return@addOnCompleteListener
                            }
                        }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_menu_spots -> {

            }
            R.id.nav_menu_history -> {

            }
            R.id.nav_menu_my_spots -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
