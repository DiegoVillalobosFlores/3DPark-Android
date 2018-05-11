package villalobos.diego.x3dpark

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import villalobos.diego.x3dpark.Data.User
import java.util.*

class LoginActivity : AppCompatActivity() {

    var user: FirebaseUser? = null
    val RC_SIGN_IN = 69

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val providers: List<AuthUI.IdpConfig> = Arrays.asList(
                AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
        )

        user = FirebaseAuth.getInstance().currentUser

        if(user == null){
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTheme(R.style.AppTheme)
                            .build(),
                    RC_SIGN_IN
            )
        }else{
            val intent = Intent(this,MainActivity::class.java)
            user?.getIdToken(true)?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val idToken:String = task.getResult().token ?: ""
                    val mUser = User(user?.displayName ?: "",
                            user?.photoUrl.toString(),
                            user?.email ?: "",
                            idToken)
                    Log.d("SIGNED IN",mUser.toString())
                    intent.putExtra("user",mUser)
                    startActivity(intent)
                    finish()
                }else{
                    Log.d("EX",task.exception.toString())
                    return@addOnCompleteListener
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val response:IdpResponse = IdpResponse.fromResultIntent(data)!!
            Log.d("SIGN IN RES",response.toString())

            if(resultCode == Activity.RESULT_OK){
                user = FirebaseAuth.getInstance().currentUser
                val intent = Intent(this,MainActivity::class.java)
                user?.getIdToken(true)?.addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val idToken:String = task.getResult().token ?: ""
                        val mUser = User(user?.displayName ?: "",
                                user?.photoUrl.toString(),
                                user?.email ?: "",
                                idToken)
                        Log.d("SIGNED IN",mUser.toString())
                        intent.putExtra("user",mUser)
                        startActivity(intent)
                        finish()
                    }else{
                        Log.d("EX",task.exception.toString())
                        return@addOnCompleteListener
                    }
                }
            }else{
                Log.d("SIGNIN","Error")
            }
        }
    }
}
