package villalobos.diego.x3dpark.Data

import java.io.Serializable

data class Fare (var day: Double = 0.0,
                 var afternoon: Double = 0.0,
                 var night: Double = 0.0,
                 var dayS:String = day.toString(),
                 var afternoonS:String = afternoon.toString(),
                 var nightS:String = night.toString()) : Serializable