package villalobos.diego.x3dpark

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result

import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.content_history.*
import villalobos.diego.x3dpark.Adapters.SpotsAdapter
import villalobos.diego.x3dpark.Data.Reservation
import villalobos.diego.x3dpark.Data.Spot
import villalobos.diego.x3dpark.Data.User
import villalobos.diego.x3dpark.R.id.toolbar

class HistoryActivity : AppCompatActivity() {

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)

        user = intent.getSerializableExtra("user") as User

        getUserReservations()
    }

    fun getUserReservations(){
        val url = getString(R.string.API_URL) + getString(R.string.API_USERS_GET_USER_RESERVATIONS)
        url.httpGet().header(Pair("Authorization",user.idToken)).responseObject { request: Request, response: Response, result: Result<ArrayList<Reservation>, FuelError> ->
            history_progress_spots.visibility = View.GONE
            history_recyler_spots.visibility = View.VISIBLE
            Log.d("HISTORY RES",response.toString())
            Log.d("HISTORY RESU",result.toString())

            when(response.statusCode){
                200 -> {
                    user.reservations = result.get()
                    drawSpotsRecycler()
                }
            }
        }
    }

    fun drawSpotsRecycler(){
        val spots :ArrayList<Spot> = ArrayList()
        for(reservaion:Reservation in user.reservations){
            spots.add(reservaion.spot)
        }
        val viewAdapter = SpotsAdapter(spots,{spot: Spot -> onSpotClicked(spot) })

        findViewById<RecyclerView>(R.id.history_recyler_spots).apply {
            setHasFixedSize(true)
            adapter = viewAdapter
        }
    }

    fun onSpotClicked(spot:Spot){
        val intent = Intent(this,SpotDetailActivity::class.java)
        intent.putExtra("user",user)
        intent.putExtra("spot",spot)
        startActivity(intent)
    }

}
