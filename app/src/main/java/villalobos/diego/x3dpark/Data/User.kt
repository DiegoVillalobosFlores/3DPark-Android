package villalobos.diego.x3dpark.Data

import android.net.Uri
import java.io.Serializable

data class User (var name:String = "",
                 var photo:String = "",
                 var email: String = "",
                 var idToken: String = "",
                 var wallet: String = "",
                 var uid: String = "",
                 var spots: ArrayList<Spot> = ArrayList()) : Serializable