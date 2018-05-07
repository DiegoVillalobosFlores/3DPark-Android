package villalobos.diego.x3dpark.Data

import android.net.Uri
import java.io.Serializable

data class User (var displayName:String = "",
                 var displayPhoto:String = "",
                 var email: String = "",
                 var idToken: String = "",
                 var wallet: String = "",
                 var spots: ArrayList<Spot> = ArrayList()) : Serializable