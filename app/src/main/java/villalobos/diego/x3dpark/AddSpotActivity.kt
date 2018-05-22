package villalobos.diego.x3dpark

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.github.kittinunf.fuel.httpPost
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import villalobos.diego.x3dpark.R

import kotlinx.android.synthetic.main.activity_add_spot.*
import kotlinx.android.synthetic.main.activity_spots_detail.*
import kotlinx.android.synthetic.main.content_add_spot.*
import org.json.JSONObject
import villalobos.diego.x3dpark.Data.Coordinates
import villalobos.diego.x3dpark.Data.Spot
import villalobos.diego.x3dpark.Data.User
import villalobos.diego.x3dpark.R.id.*

class AddSpotActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var user: User
    private val spot = Spot()
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location ->
                    val coordinates = LatLng(location.latitude,location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates,15f))
                    val latLng = LatLng(location.latitude,location.longitude)
                    spot.coordinates = Coordinates(latLng.latitude,latLng.longitude)
                }
        mMap.setOnMapClickListener { latLng: LatLng? ->
            mMap.clear()
            val marker = latLng
            val markerOptions = MarkerOptions()
            markerOptions.position(marker!!)

            mMap.addMarker(markerOptions)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,15f))

            spot.coordinates = Coordinates(latLng.latitude,latLng.longitude)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_spot)
        setSupportActionBar(toolbar)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        user = intent.getSerializableExtra("user") as User

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapito) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun onDoneClicked(v: View){
        add_spot_layout_loading.visibility = View.VISIBLE
        val url = getString(R.string.API_URL) + getString(R.string.API_SPOTS_POST_ADD_SPOT)

        val address = JSONObject()
        address.put("street",add_spot_edit_street.text.toString())
        address.put("number",add_spot_edit_number.text.toString())
        address.put("locality",add_spot_edit_neighborhood.text.toString())
        address.put("city",add_spot_edit_city.text.toString())
        address.put("cityRef","gICg1iQ3E1DD3vY3bvZW")

        val fares = JSONObject()
        fares.put("day",add_spot_edit_day.text.toString())
        fares.put("afternoon",add_spot_edit_afternoon.text.toString())
        fares.put("night",add_spot_edit_night.text.toString())
        fares.put("rate","Hr")
        fares.put("currency","ETH")

        val coord = JSONObject()
        coord.put("_latitude",spot.coordinates._latitude)
        coord.put("_longitude",spot.coordinates._longitude)

        val body = JSONObject()
        body.put("address",address)
        body.put("coordinates",coord)
        body.put("fares",fares)

        val json = JSONObject()
        json.put("spot",body)
        url.httpPost().header(Pair("Authorization",user.idToken)).header(Pair("Content-Type","application/json")).body(json.toString()).response { request, response, result ->
            Log.d("POST RES",response.toString())
            Log.d("POST RESU",result.toString())

            when(response.statusCode) {
                200 -> {
                    val intent = Intent(this,MainActivity::class.java)
                    intent.putExtra("user",user)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    add_spot_layout_loading.visibility = View.GONE
                    Toast.makeText(this,"No se pudo guardar",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
