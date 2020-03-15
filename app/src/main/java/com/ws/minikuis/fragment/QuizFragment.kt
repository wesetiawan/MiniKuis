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
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ws.minikuis.R
import kotlinx.android.synthetic.main.quiz_activity.*
import kotlinx.android.synthetic.main.fragment_quiz.*


class QuizFragment : Fragment(),View.OnClickListener{
    private lateinit var database: FirebaseDatabase
    private lateinit var quizRef: DatabaseReference
    private lateinit var winnerRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val bundle = Bundle()
    var x: TextView? = null
    var y: TextView? = null
    val TAG = "QuizFragment"
    var jawabanTepat: String? = ""
    private var USERID_KEY = "useridkey"
    private var userid_key = ""
    private var userid_key_new = ""
    var status = ""
    private var quizKey = ""
    private val selectedAnswerBG : Int = R.drawable.selected_answer_background
    private val unselectedAnswerBG: Int = R.drawable.rounded_shape_background_white
    private val wrongAnswerBG = R.drawable.wrong_answer_bg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBundle()
        firebaseGetInstance()
        getUserIdLocal()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tv_jawabanA.setOnClickListener(this)
        tv_jawabanB.setOnClickListener(this)
        tv_jawabanC.setOnClickListener(this)
        tv_jawabanD.setOnClickListener(this)

        prepareQuiz()
    }

    private fun firebaseGetInstance(){
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        quizRef = database.getReference("quiz").child(quizKey)
        winnerRef = database.getReference("winner")
    }

    private fun getBundle(){
        val bundle = this.arguments
        quizKey = bundle?.getString("quizKey").toString()
    }

    private fun selectedAnswer(v: TextView){
        timer.cancel()
        if (v.text == jawabanTepat){
            trueAnswer()
        }else{
            wrongAnswer(v)
        }
    }

    private fun resultFragment() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val fragment = ResultFragment()
        bundle.putString("quizKey",quizKey)
        fragment.arguments = bundle
        transaction?.replace(R.id.frg_holder,fragment)
        transaction?.commit()
    }

    private fun quizStatusListener(){
        Log.d(TAG,"quizStatusListener")
        shimmerLayout.visibility= View.VISIBLE
        shimmerLayout.startShimmer()
        quizRef.child("status").addValueEventListener(statusListener)
    }

    private fun stopQuizStatusListener(){
        database.goOffline()
    }

    private fun prepareQuiz(){
        shimmerLayoutCall()
        shimmerLayout.startShimmer()
        quizStatusListener()
    }

    private fun loadQuizData(){
        quizRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                errorLayoutCall()
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "getQuizData : ${dataSnapshot.value}")
                tv_pertanyaan.text = dataSnapshot.child("pertanyaan").value.toString()
                tv_jawabanA.text = dataSnapshot.child("a").value.toString()
                tv_jawabanB.text = dataSnapshot.child("b").value.toString()
                tv_jawabanC.text = dataSnapshot.child("c").value.toString()
                tv_jawabanD.text = dataSnapshot.child("d").value.toString()

                jawabanTepat = dataSnapshot.child("jawaban_tepat").value.toString()

                shimmerLayout.stopShimmer()
                quizLayoutCall()

            }

        })
    }

    private fun updateStatus(){
        quizRef.child("status").setValue("end")
    }

    private fun startQuiz(){
        loadQuizData()
        activity?.refresh_layout?.isEnabled=false
        timer.start()
    }

    private fun trueAnswer(){
        updateStatus()
        winner()
    }

    private fun wrongAnswer(v: TextView) {
        if (status!= "end" && status!="waiting"){
            selectedAnswerAnimation(v)
            buttonCondition(false)
        }else if (status == "end"){
            resultFragment()
        }

    }

    private fun winner(){
        winnerRef.child(quizKey).setValue(auth.currentUser?.displayName)
        resultFragment()
    }

    private val statusListener = object: ValueEventListener{
        override fun onCancelled(databaseError: DatabaseError) {
            errorLayoutCall()
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            status = dataSnapshot.value.toString()
            when (status) {
                "start" -> {
                    startQuiz()
                }
                "waiting" -> {
                    waitingLayoutCall()
                }
                else -> {
                    stopQuizStatusListener()
                    resultFragment()
                }
            }
        }
    }

    private val timer = object: CountDownTimer(30000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            tv_timer.text = (millisUntilFinished / 1000).toString()
            if (millisUntilFinished<10000){
                tv_timer.setTextColor(Color.RED)
                tv_alert.visibility = View.VISIBLE
            }else{
                tv_timer.setTextColor(Color.BLUE)
            }
        }
        override fun onFinish() {
            tv_timer.setTextColor(Color.BLUE)
            tv_alert.visibility = View.INVISIBLE
        }
    }

    private fun selectedAnswerAnimation(v: TextView){
        Log.d(TAG,"answerButtonCondition . TextView : $v")
        v.setBackgroundResource(wrongAnswerBG)
        if (v != tv_jawabanA && tv_jawabanA.text == jawabanTepat){
            tv_jawabanA.setBackgroundResource(selectedAnswerBG)
        }
        else if (v != tv_jawabanB && tv_jawabanB.text == jawabanTepat){
            tv_jawabanB.setBackgroundResource(selectedAnswerBG)
        }
        else if (v != tv_jawabanC && tv_jawabanC.text == jawabanTepat){
            tv_jawabanC.setBackgroundResource(selectedAnswerBG)
        }
        else{
            tv_jawabanD.setBackgroundResource(selectedAnswerBG)
        }
    }

    private fun buttonCondition(b: Boolean){
        tv_jawabanA.isEnabled = b
        tv_jawabanB.isEnabled = b
        tv_jawabanC.isEnabled = b
        tv_jawabanD.isEnabled = b
    }

    private fun waitingLayoutCall(){
        quizLayout.visibility = View.GONE
        shimmerLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        waitingLayout.visibility = View.VISIBLE
    }

    private fun quizLayoutCall(){
        waitingLayout.visibility = View.GONE
        shimmerLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        quizLayout.visibility = View.VISIBLE
    }

    private fun shimmerLayoutCall(){
        waitingLayout.visibility = View.GONE
        quizLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        shimmerLayout.visibility = View.VISIBLE
    }

    private fun errorLayoutCall(){
        waitingLayout.visibility = View.GONE
        quizLayout.visibility = View.GONE
        shimmerLayout.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }

    override fun onClick(v: View) {
        when (v.id){
            R.id.tv_jawabanA -> selectedAnswer(tv_jawabanA)
            R.id.tv_jawabanB -> selectedAnswer(tv_jawabanB)
            R.id.tv_jawabanC -> selectedAnswer(tv_jawabanC)
            R.id.tv_jawabanD -> selectedAnswer(tv_jawabanD)
        }
    }

    private fun getUserIdLocal() {
        val sharedPreferences: SharedPreferences = context!!.getSharedPreferences(USERID_KEY, Context.MODE_PRIVATE)
        userid_key_new = sharedPreferences.getString(userid_key, "").toString()
        Log.d(TAG,"UserId is : $userid_key_new")
    }

}