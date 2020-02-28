package com.ws.minikuis.ui

import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PlayGamesAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ws.minikuis.R
import com.ws.minikuis.model.User
import kotlinx.android.synthetic.main.activity_get_started.*

class GetStartedActivity : AppCompatActivity() ,View.OnClickListener{

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object{
        private const val TAG = "GetStartedActivity"
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        btn_login_google.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWIthGoogle(account!!)
            }catch (e: ApiException){
                Log.w(TAG, "Google sign in failed", e)
                updateUI()
            }
        }
    }

    private fun firebaseAuthWIthGoogle(account: GoogleSignInAccount) {
        Log.d(TAG, "FirebaseAuthWIthGoogle: " + account.id!!)

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){task->
                if (task.isSuccessful){
                    Log.d(TAG,"signInWithCrenditial:success")
                    val user = auth.currentUser
                    getUserData(user)
                }else{
                    Log.d(TAG, "signInWIthCredential:failed", task.exception)
                    Toast.makeText(this,"Authentication Failed",Toast.LENGTH_SHORT).show()
                    updateUI()
                }
            }
    }


    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun updateUI() {
        val mainActivity = Intent(this@GetStartedActivity,MainActivity::class.java)
        startActivity(mainActivity)
        finish()
    }

    private fun getUserData(user: FirebaseUser?){
        Log.d(TAG,"getUserData")
        val user = auth.currentUser
        user?.let {
            val name = user.displayName
            val uid = user.uid
            writeNewUser(uid,name.toString(),0)
        }

    }

    private fun writeNewUser(userId: String,name: String,skor: Int){
        Log.d(TAG,"writeNewUser")
        database = FirebaseDatabase.getInstance().reference
        val user = User(name,skor)
        database.child("user").child(userId).setValue(user)
        updateUI()
    }



    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_login_google -> signIn()
        }
    }
}
