package com.ws.minikuis.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.*

import com.ws.minikuis.R
import kotlinx.android.synthetic.main.quis_fragment.*

abstract class QuisFragment : Fragment(),View.OnClickListener{
    private lateinit var database: DatabaseReference
    lateinit var button: Button
    lateinit var textView: TextView
    var x: TextView? = null
    var y: TextView? = null
    val TAG = "GetStartedActivity"
    var jawabanTepat: String? = ""
    abstract var skor: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.quis_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getQuestionData()
        tv_action.text="Mulai"
        buttonConditition(false)
        tv_action.setOnClickListener(this)
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
        tv_action.text = "Pilih"
    }

    private fun endQuis(){
        tv_timer.setTextColor(Color.RED)
        buttonConditition(false)
        tv_action.text = "Lanjut"
    }

    private fun startTimer(){
        object: CountDownTimer(60000,100){
            override fun onFinish() {
                buttonConditition(false)
                endQuis()
            }
            override fun onTick(millisUntilFinished: Long) {
                tv_timer.text = (millisUntilFinished / 1000).toString()
                if (millisUntilFinished<30000){
                    tv_timer.setTextColor(Color.RED)
                }else{
                    tv_timer.setTextColor(Color.BLUE)
                }

            }

        }.start()
    }

    private fun getQuestionData(){
        shimmerLayout.visibility= View.VISIBLE
        shimmerLayout.startShimmer()
        Log.d(TAG,"getQuestion")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("pertanyaan")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.toString()
                Log.d(TAG,"Value is : $data")
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

            override fun onCancelled(databaseError: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

    }

    private fun selectedAnswer(answer: String?,v: TextView){
        Log.d(TAG,"Skor : $jawabanTepat")
        Log.d(TAG,"Skor : $skor")
        changeButton(v)
        if (answer==jawabanTepat){
            skor += 10
        }
        Log.d(TAG,"Skor : $skor")
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

    private fun getUserSkor(){
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("user")
        myRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

            }

        })
    }

    private fun updateUserSkor(){

    }

    override fun onClick(v: View) {
        when (v.id){
            R.id.tv_jawabanA -> selectedAnswer("a",tv_jawabanA)
            R.id.tv_jawabanB -> selectedAnswer("b",tv_jawabanB)
            R.id.tv_jawabanC -> selectedAnswer("c",tv_jawabanC)
            R.id.tv_jawabanD -> selectedAnswer("d",tv_jawabanD)
            R.id.tv_action -> startQuis()
        }
    }
}
