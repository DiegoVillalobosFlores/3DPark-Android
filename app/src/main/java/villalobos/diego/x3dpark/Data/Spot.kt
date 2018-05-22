package villalobos.diego.x3dpark.Data

import villalobos.diego.x3dpark.Data.Maps.Directions
import villalobos.diego.x3dpark.R.string.current
import java.io.Serializable

data class Spot (var coordinates:Coordinates = Coordinates(),
                 var fares:Fare,
                 var score:String = "",
                 var scores:Score,
                 var photo: String = "",
                 var address: Address = Address(),
                 var dimensions: Dimensions = Dimensions(),
                 val directions: Directions = Directions()) : Serializable
