package villalobos.diego.x3dpark.Data

import java.io.Serializable

data class Address (var street:String = "",
                    var number:String ="",
                    var locality:String = "",
                    var city:String = "",
                    var cityRef:String = "") : Serializable {
    fun getCompositeAddress() : String{
        return "$street $number"
    }
}