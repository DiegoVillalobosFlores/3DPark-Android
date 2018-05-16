package villalobos.diego.x3dpark.Data

import android.net.Uri
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

data class User (var name:String = "",
                 var photo:String = "",
                 var email: String = "",
                 var idToken: String = "",
                 var wallet: String = "",
                 var uid: String = "",
                 var joined:Date = Date(),
                 var score:String = "",
                 var vehicles:ArrayList<Vehicle> = ArrayList(),
                 var spots: ArrayList<Spot> = ArrayList()) : Serializable {
    fun getFormatedJoined() : String{
        return "${joined.day}/${joined.month}/${joined.year}"
    }
}