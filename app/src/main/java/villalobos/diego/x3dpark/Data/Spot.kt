package villalobos.diego.x3dpark.Data

import java.io.Serializable

data class Spot (var coordinates:Coordinates = Coordinates(),
                 var street:String = "",
                 var number:String = "",
                 var currency: String = "",
                 var fares:Fare,
                 var rate:String = "",
                 var score:String = "",
                 var scores:Score,
                 var city:String = "",
                 var locality:String = "",
                 var photo: String = "",
                 var current: String = "",
                 var dimensions: Dimensions = Dimensions()) : Serializable
