package villalobos.diego.x3dpark

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import villalobos.diego.x3dpark.R

import kotlinx.android.synthetic.main.activity_my_spots.*
import villalobos.diego.x3dpark.Data.Coordinates
import villalobos.diego.x3dpark.Data.User

class MySpotsActivity : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var coordinates: Coordinates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_spots)
        setSupportActionBar(toolbar)

        user = intent.getSerializableExtra("user") as User
        coordinates = intent.getSerializableExtra("coordinates") as Coordinates
    }

    fun onAddSpotClicked(v: View){
        val intent = Intent(this,HistoryActivity::class.java)
        intent.putExtra("user",user)
        startActivity(intent)
    }
}
