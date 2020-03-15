package com.ws.minikuis.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
import com.ws.minikuis.R
import kotlinx.android.synthetic.main.fragment_result.*

class ResultFragment : Fragment() {
    private lateinit var database: FirebaseDatabase
    private lateinit var winnerRef: DatabaseReference
    val TAG = "ResultFragment"
    private var quizKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBundle()
        firebaseGetInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getWinner()
    }

    private fun firebaseGetInstance(){
        database = FirebaseDatabase.getInstance()
        winnerRef = database.getReference("winner").child(quizKey)
    }

    private fun getBundle(){
        val bundle = this.arguments
        quizKey = bundle?.getString("quizKey").toString()
    }

    private fun getWinner(){
        winnerRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tv_name.text = dataSnapshot.value.toString()
            }

        })
    }

}
