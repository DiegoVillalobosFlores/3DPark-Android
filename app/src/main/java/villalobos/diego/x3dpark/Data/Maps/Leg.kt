package villalobos.diego.x3dpark.Data.Maps

import java.io.Serializable

data class Leg(val distance: Distance = Distance(),
               val duration: Distance = Distance()) : Serializable