package com.ws.minikuis.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
import com.ws.minikuis.R
import kotlinx.android.synthetic.main.fragment_result.*

class ResultFragment : Fragment() {
    private lateinit var database: FirebaseDatabase
    private lateinit var getWinnerRef: DatabaseReference
    private lateinit var quizRef: DatabaseReference
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
    }

    private fun getBundle(){
        val bundle = this.arguments
        quizKey = bundle?.getString("quizKey").toString()
    }

    private fun getWinner(){
        getWinnerRef = database.getReference("winner").child(quizKey)
        getWinnerRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
               if (dataSnapshot.value == null){
                   tv_name.text = "Pemenang Belum Ada"
                }else{
                   tv_name.text = dataSnapshot.value.toString()
                }
            }
        })
    }

}
