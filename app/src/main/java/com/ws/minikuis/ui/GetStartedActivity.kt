package com.ws.minikuis.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.ws.minikuis.R
import com.ws.minikuis.model.User
import kotlinx.android.synthetic.main.activity_get_started.*

class GetStartedActivity : AppCompatActivity() ,View.OnClickListener{

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    var uName: String = ""
    var uId: String = ""
    var foundUser: Boolean = true

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
                    getUserAuthData(user)
                    checkUserDataAlready(uId)
                    if (!foundUser){
                        writeNewUser(uId,uName)
                    }else{
                        updateUI()
                    }
                }else{
                    Log.d(TAG, "signInWIthCredential:failed", task.exception)
                    Toast.makeText(this,"Authentication Failed",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserDataAlready(userId: String) {
        Log.d(TAG,"getUserID")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("user")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(userId)){
                    foundUser(true)
                }else{
                    foundUser(false)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun updateUI() {
        val mainActivity = Intent(this,QuisActivity::class.java)
        startActivity(mainActivity)
        finish()
    }

    private fun foundUser(b: Boolean) {
        foundUser = b
    }

    private fun getUserAuthData(user: FirebaseUser?){
        Log.d(TAG,"getUserData")
        user?.let {
            uName = user.displayName.toString()
            uId = user.uid
        }
    }

    private fun writeNewUser(userId: String,name: String){
        Log.d(TAG,"writeNewUser")
            database = FirebaseDatabase.getInstance().reference
            val user = User(name,0)
            database.child("user").child(userId).setValue(user)
            updateUI()

    }



    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_login_google -> signIn()
        }
    }
}

