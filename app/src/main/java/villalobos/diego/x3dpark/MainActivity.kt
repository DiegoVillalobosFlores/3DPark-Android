package villalobos.diego.x3dpark

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.graphics.drawable.AnimationUtilsCompat
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
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
import android.widget.Toast
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.nitrico.lastadapter.LastAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import villalobos.diego.x3dpark.Adapters.Spots
import villalobos.diego.x3dpark.Data.Spot
import villalobos.diego.x3dpark.Data.User
import villalobos.diego.x3dpark.R.id.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var user:User
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nav_view.setNavigationItemSelectedListener(this)

        user = intent.getSerializableExtra("user") as User

        Log.d("TOKEN",user.idToken)

        val inflatedView = nav_view.inflateHeaderView(R.layout.nav_header_main)
        val id = inflatedView.findViewById<ImageView>(R.id.nav_image_profile)

        Log.wtf("PHOTO",user.displayPhoto)

        if(user.displayPhoto != "null"){
            Glide.with(this)
                    .load(user.displayPhoto)
                    .into(id)
        }
        val name = inflatedView.findViewById<TextView>(R.id.nav_text_name)
        name.text = user.displayName



        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.main_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        mMap.isMyLocationEnabled = true

    }

    fun toggleNearbyDrawer(v:View){
        if(main_linear_drawer.visibility == View.GONE){
            main_linear_drawer.visibility = View.VISIBLE
            val leftSwipe = AnimationUtils.loadAnimation(this,R.anim.layout_open_left)
            main_linear_drawer.startAnimation(leftSwipe)
        }else{
            main_linear_drawer.visibility = View.GONE
        }
    }

    override fun onMapReady(p0: GoogleMap) {

        mMap = p0
        val url = getString(R.string.API_URL) + getString(R.string.API_POSTS_GET_ALL_POSTS)

        url.httpGet().header(Pair("authorization",user.idToken)).responseObject { request: Request, response: Response, result: Result<ArrayList<Spot>, FuelError> ->
            Log.d("REQ",request.toString())
            Log.d("RES",response.responseMessage)
            Log.wtf("RES",result.toString())

            user.spots = result.get()

            val spots = user.spots as List<Spot>

            for (spot:Spot in spots){
                spot.fares.dayS = spot.fares.day.toString()
                spot.fares.afternoonS = spot.fares.afternoon.toString()
                spot.fares.nightS = spot.fares.night.toString()

                val sydney = LatLng(spot.coordinates._latitude,spot.coordinates._longitude)
                mMap.addMarker(MarkerOptions().position(sydney).title("${spot.street} ${spot.number}"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,16f))



            }
            viewManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
            viewAdapter = Spots(user.spots,{spot:Spot -> onSpotPressed(spot)})

            recyclerView = findViewById<RecyclerView>(R.id.main_recycler_spots).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }

            /*LastAdapter(spots,BR.item)
                    .map<Spot>(R.layout.recycler_spots)
                    .into(main_recycler_spots)*/
        }

        setUpMap()
    }

    fun onSpotPressed(spot:Spot){

    }

    override fun onBackPressed() {
        Log.d("BAKC","true")
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
