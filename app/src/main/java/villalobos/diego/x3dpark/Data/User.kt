package villalobos.diego.x3dpark.Data

import android.net.Uri
import villalobos.diego.x3dpark.R.string.joined
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

data class User (var name:String = "",
                 var photo:String = "",
                 var email: String = "",
                 var idToken: String = "",
                 var wallet: String = "",
                 var balance: Double = 1.0,
                 var uid: String = "",
                 var joined:Date = Date(),
                 var score:String = "",
                 var vehicles:ArrayList<Vehicle> = ArrayList(),
                 var spots: ArrayList<Spot> = ArrayList(),
                 var reservations: ArrayList<Reservation> = ArrayList()) : Serializable {

    fun getFormatedJoined() : String{
        return "${joined.day}/${joined.month}/${joined.year}"
    }

    fun getFormatedBalance() : String {
        return "Balance: $balance ETH"
    }
}