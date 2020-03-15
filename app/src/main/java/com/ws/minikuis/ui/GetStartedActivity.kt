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

class GetStartedActivity : AppCompatActivity() ,View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private var TAG = "GetStartedActivity"
    var uName: String = ""
    var uId: String = ""
    var USERID_KEY = "useridkey"
    var userid_key = ""
    var currentType = ""

    companion object{
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)
        btn_login_google.setOnClickListener(this)
        btn_join_quiz.setOnClickListener(this)
        btn_create_quiz.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("user")
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
            }
        }
    }

    private fun firebaseAuthWIthGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){task->
                if (task.isSuccessful){
                    val user = auth.currentUser
                    getUserAuthData(user)
                    checkUserDataAlready()
                }else{
                    Toast.makeText(this,"Authentication Failed",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserDataAlready() {
        myRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(uId)){
                    foundUser(true)
                }else{
                    foundUser(false)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun signIn() {
        btn_login_google.visibility= View.INVISIBLE
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun moveQuizActivity() {
        val mainActivity = Intent(this,QuizActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainActivity)
        finish()
    }

    private fun moveQuizCreatorActivity() {
        val mainActivity = Intent(this,QuizCreatorActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainActivity)
        finish()
    }

    private fun foundUser(b: Boolean) {
        when {
            !b -> { writeNewUser(uId,uName)
            }
            else -> { moveActivitySwitch(currentType)
            }
        }
    }

    private fun getUserAuthData(user: FirebaseUser?){
        user?.let {
            uName = user.displayName.toString()
            uId = user.uid
            Log.d(TAG, "uid:$uId")
        }
        saveUserIdToLocal(uId)
    }

    private fun writeNewUser(userId: String,name: String){
        val user = User(name)
        myRef.child(userId).setValue(user)
        moveQuizActivity()
    }

    private fun saveUserIdToLocal(uId: String) {
        val sharedPreferences = getSharedPreferences(USERID_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(userid_key, uId)
        editor.apply()
        editor.commit()
    }

    private fun moveActivitySwitch (type: String){
        if (type == "join"){
            moveQuizActivity()
        }else{
            moveQuizCreatorActivity()
        }
    }

    private fun loginLayout (type: String){
        currentType = type
        main_login_layout.visibility= View.GONE
        login_layout.visibility= View.VISIBLE
    }

    override fun onBackPressed() {
        if (login_layout.visibility == View.VISIBLE){
            main_login_layout.visibility= View.VISIBLE
            login_layout.visibility= View.GONE
        }else{
            super.onBackPressed()
        }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_login_google -> signIn()
            R.id.btn_join_quiz -> loginLayout("join")
            R.id.btn_create_quiz -> loginLayout("create")
        }
    }

}

