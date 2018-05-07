package villalobos.diego.x3dpark.Data

import android.databinding.InverseMethod
import android.util.Log

class Converter{
    @InverseMethod("toDouble")
    fun toString(value:Double) : String {
        Log.wtf("CONVERTING",value.toString())
        return "" + value
    }

    fun toDouble(value: String) : Double {
        return value.toDouble()
    }
}