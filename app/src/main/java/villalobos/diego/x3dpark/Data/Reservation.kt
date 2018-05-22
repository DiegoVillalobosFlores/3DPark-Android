package villalobos.diego.x3dpark.Data

import java.io.Serializable

data class Reservation (val from:String = "",
                        val to:String = "",
                        val total:String = "",
                        val spot: Spot = Spot(),
                        val id:String = "") : Serializable