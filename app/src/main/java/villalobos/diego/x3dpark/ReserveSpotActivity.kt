package villalobos.diego.x3dpark

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.github.kittinunf.fuel.httpGet
import kotlinx.android.synthetic.main.activity_reserve_spot.*
import nl.dionsegijn.steppertouch.OnStepCallback
import nl.dionsegijn.steppertouch.StepperTouch
import villalobos.diego.x3dpark.Data.Reservation
import villalobos.diego.x3dpark.Data.Spot
import villalobos.diego.x3dpark.Data.User

class ReserveSpotActivity : AppCompatActivity() {

    private lateinit var reservation: Reservation
    private lateinit var spot: Spot
    private lateinit var user: User
    private lateinit var estimatedCost:String
    private var hours = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve_spot)

        spot = intent.getSerializableExtra("spot") as Spot
        user = intent.getSerializableExtra("user") as User


    }


}
