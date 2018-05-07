package villalobos.diego.x3dpark

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import villalobos.diego.x3dpark.Data.Spot
import villalobos.diego.x3dpark.Data.User

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var user:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        user = intent.getSerializableExtra("user") as User

        Log.d("TOKEN",user.idToken)

        val inflatedView = nav_view.inflateHeaderView(R.layout.nav_header_main)
        val id = inflatedView.findViewById<ImageView>(R.id.nav_image_profile)

        Log.wtf("PHOTO",user.displayPhoto)

        if(user.displayPhoto != "null"){
            Glide.with(this)
                    .load(user.displayPhoto)
                    .into(id)
        }
        val name = inflatedView.findViewById<TextView>(R.id.nav_text_name)
        name.text = user.displayName

        val url = getString(R.string.API_URL) + getString(R.string.API_POSTS_GET_ALL_POSTS)

        url.httpGet().header(Pair("authorization",user.idToken)).responseObject { request: Request, response: Response, result: Result<ArrayList<Spot>, FuelError> ->
            Log.d("REQ",request.toString())
            Log.d("RES",response.responseMessage)
            Log.wtf("RES",result.toString())

            user.spots = result.get()

            val spots = user.spots as List<Spot>

            for (spot:Spot in spots){
                spot.fares.dayS = spot.fares.day.toString()
                spot.fares.afternoonS = spot.fares.afternoon.toString()
                spot.fares.nightS = spot.fares.night.toString()
            }

            LastAdapter(spots,BR.item)
                    .map<Spot>(R.layout.recycler_spots)
                    .into(main_recycler_spots)
        }
    }

    override fun onBackPressed() {
        Log.d("BAKC","true")
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                val intent = Intent(this,LoginActivity::class.java)
                                startActivity(intent)
                                return@addOnCompleteListener
                            }
                        }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_menu_spots -> {

            }
            R.id.nav_menu_history -> {

            }
            R.id.nav_menu_profile -> {
                val intent = Intent(this,ProfileActivity::class.java)
                intent.putExtra("user",user)
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
