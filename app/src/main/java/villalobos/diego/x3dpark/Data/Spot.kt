package villalobos.diego.x3dpark.Data

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.recycler_spots.view.*
import villalobos.diego.x3dpark.MapsActivity
import villalobos.diego.x3dpark.R
import java.io.Serializable

class Spot (var coordinates:Coordinates = Coordinates(),
            var street:String = "",
            var number:String = "",
            var currency: String = "",
            var fares:Fare,
            var rate:String = "",
            var score:String = "",
            var scores:Score,
            var city:String = "",
            var locality:String = "") : Serializable {

    fun onClick(v: View){
        val intent = Intent(v.context,MapsActivity::class.java)
        intent.putExtra("spot",this)
        v.context.startActivity(intent)
    }

}
