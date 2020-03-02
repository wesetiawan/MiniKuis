package com.ws.minikuis.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.ws.minikuis.R
import kotlinx.android.synthetic.main.quis_fragment.*

class QuesFragment : Fragment(),View.OnClickListener{
    private lateinit var database: DatabaseReference
    lateinit var button: Button
    lateinit var textView: TextView
    var x: TextView? = null
    var y: TextView? = null
    val TAG = "QuestFragment"
    var jawabanTepat: String? = ""
    var jawabanTerpilih: String? = ""
    var skor: Int = 0
    var USERID_KEY = "useridkey"
    var userid_key = ""
    var userid_key_new = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.quis_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getUserIdLocal()
        getUserData()
        getQuestionData()
        btn_action.text="Mulai"
        buttonConditition(false)
        btn_action.setOnClickListener(this)
        tv_jawabanA.setOnClickListener(this)
        tv_jawabanB.setOnClickListener(this)
        tv_jawabanC.setOnClickListener(this)
        tv_jawabanD.setOnClickListener(this)
    }

    private fun buttonConditition(b: Boolean){
        tv_jawabanA.isEnabled = b
        tv_jawabanB.isEnabled = b
        tv_jawabanC.isEnabled = b
        tv_jawabanD.isEnabled = b
    }

    private fun startQuis(){
        startTimer()
        buttonConditition(true)
        btn_action.text = "Pilih"
        btn_action.isEnabled = false
    }

    private fun endQuis(){
        tv_timer.setTextColor(Color.RED)
        buttonConditition(false)
    }

    private fun startTimer(){
        object: CountDownTimer(30000,100){
            override fun onFinish() {
                buttonConditition(false)
                endQuis()
                btn_action.isEnabled = true
            }
            override fun onTick(millisUntilFinished: Long) {
                tv_timer.text = (millisUntilFinished / 1000).toString()
                if (millisUntilFinished<10000){
                    tv_timer.setTextColor(Color.RED)
                }else{
                    tv_timer.setTextColor(Color.BLUE)
                }

            }

        }.start()
    }

    private fun selectedAnswer(answer: String?,v: TextView){
        changeButton(v)
        jawabanTerpilih = answer
    }

    fun changeButton(v: TextView){
        if (x!=null){
            y = v
            x?.isEnabled = true
            y?.isEnabled = false
            x=y
        }else{
            x = v
            x?.isEnabled = false
        }
    }

    private fun getQuestionData(){
        shimmerLayout.visibility= View.VISIBLE
        shimmerLayout.startShimmer()
        Log.d(TAG,"getQuestion")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("pertanyaan")
        Log.d(TAG, "myRef$myRef")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tv_pertanyaan.text = dataSnapshot.child("soal").value.toString()
                tv_jawabanA.text = dataSnapshot.child("jawaban").child("a").value.toString()
                tv_jawabanB.text = dataSnapshot.child("jawaban").child("b").value.toString()
                tv_jawabanC.text = dataSnapshot.child("jawaban").child("c").value.toString()
                tv_jawabanD.text = dataSnapshot.child("jawaban").child("d").value.toString()

                jawabanTepat = dataSnapshot.child("jawaban_tepat").value.toString()

                shimmerLayout.stopShimmer()
                shimmerLayout.visibility= View.GONE
                quis_layout.visibility=View.VISIBLE
            }

        })

    }

    private fun getUserData(){
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("user").child(userid_key_new)
        myRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tv_userName.text = dataSnapshot.child("name").value.toString()
                tv_skor.text = dataSnapshot.child("skor").value.toString()
            }

        })
    }

    private fun getUserIdLocal() {
        val sharedPreferences: SharedPreferences = context!!.getSharedPreferences(USERID_KEY, Context.MODE_PRIVATE)
        userid_key_new = sharedPreferences.getString(userid_key, "").toString()
        Log.d(TAG,"UserId is : $userid_key_new")
    }

    private fun updateUserSkor(){
        btn_action.text = "Mulai"
    }

    private fun sendAnswer(){
        btn_action.text = "Lanjut"
    }

    private fun btnAction(){
        when (btn_action.text) {
            "Mulai" -> {
                startQuis()
            }
            "Pilih" -> {
                sendAnswer()
            }
            else -> {
                getQuestionData()
                updateUserSkor()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id){
            R.id.tv_jawabanA -> selectedAnswer("a",tv_jawabanA)
            R.id.tv_jawabanB -> selectedAnswer("b",tv_jawabanB)
            R.id.tv_jawabanC -> selectedAnswer("c",tv_jawabanC)
            R.id.tv_jawabanD -> selectedAnswer("d",tv_jawabanD)
            R.id.btn_action ->btnAction()
        }
    }
}
