package villalobos.diego.x3dpark

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_spots_detail.*
import villalobos.diego.x3dpark.Data.Spot

class SpotDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var spot: Spot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spots_detail)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        spot = intent.getSerializableExtra("spot") as Spot

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
        if(spots_detail_layout_image.visibility == View.VISIBLE){
            spots_detail_layout_image.visibility = View.GONE
        }else{
            spots_detail_layout_image.visibility = View.VISIBLE
        }
    }
}
