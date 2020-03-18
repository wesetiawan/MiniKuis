package com.ws.minikuis.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.ws.minikuis.R
import kotlinx.android.synthetic.main.fragment_quiz_key.*


class QuizKeyFragment : Fragment(), View.OnClickListener {
    private lateinit var database: FirebaseDatabase
    private lateinit var quizRef: DatabaseReference
    private val TAG = "QuizKeyFragment"
    private val bundle = Bundle()
    private var quizKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_quiz_key, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_key_join.setOnClickListener(this)
    }


    private fun findQuiz(){
        quizKey = et_quiz_key.text.toString()
        quizRef = database.getReference("quiz").child(quizKey)
        quizRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null){
                    tv_error.visibility = View.GONE
                    input_key_layout.visibility = View.GONE
                    quizFragment()
                }else{
                    tv_error.visibility = View.VISIBLE
                }
            }

        })
    }

    private fun quizFragment() {
        val transaction = activity?.supportFragmentManager!!.beginTransaction()
        val fragment = QuizFragment()
        bundle.putString("quizKey",quizKey)
        Log.d(TAG,"bundle : ${bundle.getString("quizKey")}")
        fragment.arguments = bundle;
        transaction.replace(R.id.frg_holder,fragment,"quizFrg")
        transaction.commit()
    }

    override fun onClick(v: View) {
        when (v.id){
            R.id.btn_key_join -> findQuiz()
        }
    }
}

