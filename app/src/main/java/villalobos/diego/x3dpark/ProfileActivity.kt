package villalobos.diego.x3dpark

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result

import kotlinx.android.synthetic.main.content_profile.*
import villalobos.diego.x3dpark.Adapters.VehiclesAdapter
import villalobos.diego.x3dpark.Data.User
import villalobos.diego.x3dpark.Data.Vehicle

class ProfileActivity : AppCompatActivity() {

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        user = intent.getSerializableExtra("user") as User

        getUserData()
    }

    fun getUserData(){
        profile_scroll_main.visibility = View.GONE
        profile_progress_main.visibility = View.VISIBLE
        val url = getString(R.string.API_URL) + getString(R.string.API_USERS_GET_USER_DATA)
        val params = listOf("vehicles" to true)
        url.httpGet(params).header(Pair("authorization",user.idToken)).responseObject { request: Request, response: Response, result: Result<User, FuelError> ->
            Log.d("PROFILE REQ",request.toString())
            Log.d("PROFILE RES",response.toString())
            Log.d("PROFILE RESULT",result.toString())
            when(response.statusCode){
                200 -> {
                    val userData = result.get()
                    user.vehicles = userData.vehicles
                    user.score = userData.score
                    user.joined = userData.joined
                    user.wallet = userData.wallet

                    drawProfileDetails()
                    drawVehiclesRecycler()
                }
            }
        }
    }

    fun drawProfileDetails(){
        val joined = "${getString(R.string.joined)}: ${user.getFormatedJoined()}"
        Glide.with(this).load(user.photo).into(profile_image_profile)
        Glide.with(this).load(user.photo).into(profile_image_large)
        profile_text_name.text = user.name
        profile_text_joined.text = joined
        profile_text_score.text = user.score
        profile_text_wallet.text = user.wallet
        profile_text_balance.text = user.getFormatedBalance()
    }

    fun drawVehiclesRecycler(){
        profile_progress_main.visibility = View.GONE
        profile_scroll_main.visibility = View.VISIBLE
        val viewAdapter = VehiclesAdapter(user.vehicles,{ vehicle: Vehicle -> onVehicleClicked(vehicle) })

        findViewById<RecyclerView>(R.id.profile_recycler_cars).apply {
            setHasFixedSize(true)
            adapter = viewAdapter
            isNestedScrollingEnabled = false
        }
    }

    fun enlargePhoto(v:View){
        if(profile_layout_image.visibility == View.VISIBLE){
            profile_layout_image.visibility = View.GONE
        }else{
            profile_layout_image.visibility = View.VISIBLE
        }
    }

    fun onProfileImageClicked(v:View){
        Glide.with(this).load(user.photo).into(profile_image_large)
        profile_layout_image.visibility = View.VISIBLE
    }

    fun onVehicleClicked(vehicle: Vehicle){
        Glide.with(this).load(vehicle.photo).into(profile_image_large)
        profile_layout_image.visibility = View.VISIBLE
    }

}
