package villalobos.diego.x3dpark.Data

import java.io.Serializable

data class Fare (var day: Double = 0.0,
                 var afternoon: Double = 0.0,
                 var night: Double = 0.0,
                 val current: Double = 0.0,
                 val rate:String = "Hr",
                 val currency:String = "ETH") : Serializable