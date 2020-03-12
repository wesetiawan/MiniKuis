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
import com.google.firebase.database.*
import com.ws.minikuis.R
import kotlinx.android.synthetic.main.quiz_activity.*
import kotlinx.android.synthetic.main.quiz_fragment.*

class QuizFragment : Fragment(),View.OnClickListener{
    private lateinit var database: FirebaseDatabase
    private lateinit var quizRef: DatabaseReference
    var x: TextView? = null
    var y: TextView? = null
    val TAG = "QuestFragment"
    var jawabanTepat: String? = ""
    var jawabanTerpilih: String? = ""
    var USERID_KEY = "useridkey"
    var userid_key = ""
    var userid_key_new = ""
    var status = ""
    private val selectedAnswerBackgroundColor : Int = R.drawable.selected_answer_background
    private val unselectedAnswerBackgroundColor: Int = R.drawable.rounded_shape_background_white

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
        getUserIdLocal()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.quiz_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //getStatusListener()
        tv_alert.visibility = View.INVISIBLE
        buttonCondition(false)
        btn_key_join.setOnClickListener(this)
        tv_jawabanA.setOnClickListener(this)
        tv_jawabanB.setOnClickListener(this)
        tv_jawabanC.setOnClickListener(this)
        tv_jawabanD.setOnClickListener(this)
    }

    private fun findQuiz(){
        val s = et_quiz_key.text.toString()
        quizRef = database.getReference("quiz").child(s)
        quizRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null){
                    tv_error.visibility = View.GONE
                    input_key_layout.visibility = View.GONE
                    getStatusListener()
                }else{
                    tv_error.visibility = View.VISIBLE
                }
            }

        })
    }

    private fun buttonCondition(b: Boolean){
        tv_jawabanA.isEnabled = b
        tv_jawabanB.isEnabled = b
        tv_jawabanC.isEnabled = b
        tv_jawabanD.isEnabled = b
    }

    private fun selectedAnswer(v: TextView){
        timer.cancel()
        selectedAnswerAnimation(v)
        generateAnswer(v)
        Log.d(TAG,"jawaban Terpilih : $jawabanTerpilih")
        if (jawabanTepat == jawabanTerpilih){
            updateStatus("end")

            resultFragment()
        }else{
            trueAnswer()
        }
    }

    private fun generateAnswer(v: TextView) {
        jawabanTerpilih = v.text.toString()
    }

    private fun selectedAnswerAnimation(v: TextView?){
        Log.d(TAG,"answerButtonCondition . TextView : $v")
        if (x!=null){
            y = v
            x?.setBackgroundResource(unselectedAnswerBackgroundColor)
            x?.isEnabled = true
            y?.setBackgroundResource(selectedAnswerBackgroundColor)
            y?.isEnabled = false
            x=y
        }else{
            x = v
            x?.setBackgroundResource(selectedAnswerBackgroundColor)
            x?.isEnabled = false
        }
    }

    private fun reset(){
        tv_jawabanA.setBackgroundResource(unselectedAnswerBackgroundColor)
        tv_jawabanB.setBackgroundResource(unselectedAnswerBackgroundColor)
        tv_jawabanC.setBackgroundResource(unselectedAnswerBackgroundColor)
        tv_jawabanD.setBackgroundResource(unselectedAnswerBackgroundColor)
        activity?.refresh_layout?.isEnabled=true
    }

    private fun resultFragment() {
        val resultFragment = ResultFragment.newInstance()
        val fragmentManager = activity!!.supportFragmentManager
        val fragMeTransaction = fragmentManager.beginTransaction()
        fragMeTransaction.replace(R.id.frg_holder,resultFragment)
        fragMeTransaction.commit()
    }

    private fun getStatusListener(){
        shimmerLayout.visibility= View.VISIBLE
        shimmerLayout.startShimmer()
        quizRef.child("status").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "statusListener : ${dataSnapshot.value}")
                status = dataSnapshot.value.toString()
                if (status == "start"){
                    getQuizData()
                }else{
                    waitingLayout.visibility=View.VISIBLE
                    shimmerLayout.visibility= View.GONE
                    quis_layout.visibility=View.GONE
                }
            }

        })
    }

    private fun getQuizData(){
        Log.d(TAG, "getQuizData")
        shimmerLayout.startShimmer()

        waitingLayout.visibility=View.GONE
        quis_layout.visibility= View.GONE
        shimmerLayout.visibility= View.VISIBLE

        quizRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                shimmerLayout.visibility= View.GONE
                quis_layout.visibility= View.GONE
                error_layout.visibility= View.VISIBLE
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "getQuizData : ${dataSnapshot.value}")
                tv_pertanyaan.text = dataSnapshot.child("soal").value.toString()
                tv_jawabanA.text = dataSnapshot.child("a").value.toString()
                tv_jawabanB.text = dataSnapshot.child("b").value.toString()
                tv_jawabanC.text = dataSnapshot.child("c").value.toString()
                tv_jawabanD.text = dataSnapshot.child("d").value.toString()

                jawabanTepat = dataSnapshot.child("jawaban_tepat").value.toString()

                shimmerLayout.stopShimmer()
                shimmerLayout.visibility= View.GONE
                quis_layout.visibility=View.VISIBLE
                startQuiz()

            }

        })

    }

    private fun updateStatus(s: String){
        quizRef.child("satus").setValue(s)
    }

    private fun startQuiz(){
        activity?.refresh_layout?.isEnabled=false
        timer.start()
        buttonCondition(true)
    }

    private fun trueAnswer(){

    }

    private fun wrongAnswer(){

    }

    private fun endQuiz(){
        timer.cancel()
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
            buttonCondition(false)
            tv_timer.setTextColor(Color.BLUE)
            tv_alert.visibility = View.INVISIBLE
        }
    }

    override fun onClick(v: View) {
        when (v.id){
            R.id.tv_jawabanA -> selectedAnswer(tv_jawabanA)
            R.id.tv_jawabanB -> selectedAnswer(tv_jawabanB)
            R.id.tv_jawabanC -> selectedAnswer(tv_jawabanC)
            R.id.tv_jawabanD -> selectedAnswer(tv_jawabanD)
            R.id.btn_key_join -> findQuiz()
        }
    }

    private fun getUserIdLocal() {
        val sharedPreferences: SharedPreferences = context!!.getSharedPreferences(USERID_KEY, Context.MODE_PRIVATE)
        userid_key_new = sharedPreferences.getString(userid_key, "").toString()
        Log.d(TAG,"UserId is : $userid_key_new")
    }
}
