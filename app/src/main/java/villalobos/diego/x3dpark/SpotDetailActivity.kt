package villalobos.diego.x3dpark

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.httpGet

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_spots_detail.*
import nl.dionsegijn.steppertouch.OnStepCallback
import nl.dionsegijn.steppertouch.StepperTouch
import villalobos.diego.x3dpark.Data.Spot
import villalobos.diego.x3dpark.Data.User

class SpotDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var spot: Spot
    private lateinit var user: User
    private lateinit var estimatedCost:String
    private var hours = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spots_detail)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        spot = intent.getSerializableExtra("spot") as Spot
        user = intent.getSerializableExtra("user") as User

        bind()
    }

    fun bind(){
        val address = spot.address
        val fares = spot.fares
        val addressS = "${address.street} ${address.number}"
        val addressSFull = "$addressS, ${address.locality}, ${address.city}"
        val distance = spot.directions.route.leg.distance
        val duration = spot.directions.route.leg.duration
        val distanceS = "${duration.text} / ${distance.text}"
        val currentFare = "${fares.current} ${fares.currency} / ${fares.rate}"
        val dayFare = "${fares.day} ${fares.currency} / ${fares.rate}"
        val afternoonFare = "${fares.afternoon} ${fares.currency} / ${fares.rate}"
        val nightFare = "${fares.night} ${fares.currency} / ${fares.rate}"

        spots_detail_text_address_street.text = addressS
        spots_detail_text_address_full.text = addressSFull
        spots_detail_text_score.text = spot.score
        spots_detail_text_distance.text = distanceS
        spots_detail_text_fare_current.text = currentFare
        spots_detail_text_fare_day.text = dayFare
        spots_detail_text_fare_afternoon.text = afternoonFare
        spots_detail_text_fare_night.text = nightFare

        Glide.with(this).load(spot.photo).into(spots_detail_image_large)
        Glide.with(this).load(spot.photo).into(spots_detail_image_small)

        estimatedCost = "${getString(R.string.estimatedCost)} ${spot.fares.current} ${spot.fares.currency}"
        reserve_text_cost.text = estimatedCost

        findViewById<StepperTouch>(R.id.reserve_stepper_hours).apply {
            stepper.setMin(1)
            stepper.setValue(1)
            stepper.setMax(168)
            stepper.addStepCallback(object : OnStepCallback {
                override fun onStep(value: Int, positive: Boolean) {
                    hours = value
                    estimatedCost = "${getString(R.string.estimatedCost)} ${spot.fares.current * value} ${spot.fares.currency}"
                    reserve_text_cost.text = estimatedCost
                }
            })
        } as StepperTouch
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val marker = LatLng(spot.coordinates._latitude,spot.coordinates._longitude)
        val address = spot.address

        val markerOptions = MarkerOptions()
        markerOptions.position(marker)
        markerOptions.title("${address.street} ${address.number} ")

        val locationmarker = mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,15f))

        //locationmarker.showInfoWindow()
    }

    fun enlargePhoto(v: View){
        reserve_scroll_reservation.visibility = View.GONE
        if(spots_detail_layout_image.visibility == View.VISIBLE){
            spots_detail_layout_image.visibility = View.GONE
        }else{
            spots_detail_layout_image.visibility = View.VISIBLE
        }
    }

    fun onReserveSpot(v:View){
        spots_detail_image_large.visibility = View.GONE
        reserve_scroll_reservation.visibility = View.VISIBLE
        spots_detail_layout_image.visibility = View.VISIBLE
    }

    fun reserveSpot(v: View){
        reserve_button_reserve.visibility = View.GONE
        reserve_progress_reserving.visibility = View.VISIBLE
        val url = getString(R.string.API_URL) + getString(R.string.API_POSTS_POST_RESERVE_SPOT)
        url.httpGet(listOf("spot" to spot.id,"hours" to hours)).header(Pair("authorization",user.idToken)).response { request, response, result ->
            reserve_progress_reserving.visibility = View.GONE
            reserve_button_reserve.visibility = View.VISIBLE
            Log.d("RESERVE RES",response.toString())
            Log.d("RESERVE RESU",result.toString())

            when(response.statusCode){
                200 -> {
                    Toast.makeText(this,"Reservado", Toast.LENGTH_LONG).show()
                    reserve_fab_reserve.visibility = View.GONE
                    val intent = Intent(this,HistoryActivity::class.java)
                    intent.putExtra("user",user)
                    startActivity(intent)
                }
                else -> {
                    Toast.makeText(this,"No se pudo reservar", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
