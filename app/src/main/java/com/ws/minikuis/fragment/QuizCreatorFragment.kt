package com.ws.minikuis.fragment

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import com.ws.minikuis.R
import com.ws.minikuis.model.Soal
import kotlinx.android.synthetic.main.fragment_quiz_creator.*


class QuizCreatorFragment : Fragment(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {

    private lateinit var database: FirebaseDatabase
    private lateinit var quizRef: DatabaseReference
    private lateinit var quizControlRef: DatabaseReference
    private lateinit var quizWinnerRef: DatabaseReference
    private var USERID_KEY = "useridkey"
    private var userid_key = ""
    private var userid_key_new = ""
    val TAG = "QuizCreatorFragment"
    var quizKey = ""
    private var jawabanTepat =""
    lateinit var jawabanA:String
    lateinit var jawabanB:String
    lateinit var jawabanC:String
    lateinit var jawabanD:String
    var pertanyaan = ""
    private var x:Int = 0
    private var y:Int = 0
    var viewX: CheckBox? = null
    var viewY: CheckBox? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getUserIdLocal()
        firebaseGetInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz_creator, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_mulai.setOnClickListener(this)
        btn_buat.setOnClickListener(this)
        btn_stop.setOnClickListener(this)
        btn_hasil.setOnClickListener(this)
        cb_jawabanA.setOnCheckedChangeListener(this)
        cb_jawabanB.setOnCheckedChangeListener(this)
        cb_jawabanC.setOnCheckedChangeListener(this)
        cb_jawabanD.setOnCheckedChangeListener(this)
        layoutSwitch(quiz_creator_layout)
    }

    private fun firebaseGetInstance(){
        database = FirebaseDatabase.getInstance()
        quizRef = database.getReference("quiz")
        quizControlRef = database.getReference("quiz").child(quizKey)
        quizWinnerRef = database.getReference("winner")
    }

    private fun generateQuiz(){
        fieldCheck()
        if (pertanyaan.nonEmpty()&&jawabanA.nonEmpty()&&jawabanB.nonEmpty()&&jawabanC.nonEmpty()&&jawabanD.nonEmpty()&&quizKey.nonEmpty() && jawabanTepat.isNotEmpty()&&jawabanTepat.isNotBlank()){
            writeNewQuiz()
        }else{
            fieldCheck()
        }
    }

    private fun fieldCheck() {
        pertanyaan = et_pertanyaan.getOrShowError("Pertanyaan Tidak Boleh Kosong")
        jawabanA = et_jawabanA.getOrShowError(et_jawabanA.hint.toString()+" Tidak Boleh Kosong")
        jawabanB = et_jawabanB.getOrShowError(et_jawabanB.hint.toString()+" Tidak Boleh Kosong")
        jawabanC = et_jawabanC.getOrShowError(et_jawabanC.hint.toString()+" Tidak Boleh Kosong")
        jawabanD = et_jawabanD.getOrShowError(et_jawabanD.hint.toString()+" Tidak Boleh Kosong")
        quizKey = et_key.getOrShowError("Key Tidak Boleh Kosong")

    }

    fun checkQuizAuth(string: String) : Boolean{
        return userid_key_new == string

    }

    private fun checkSoalKeyAlreadyOrNot() {
        quizKey = et_key.getOrShowError(" Key tidak boleh kosong")
        if (quizKey.nonEmpty()){
            quizRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild(quizKey)){
                        val creator = dataSnapshot.child(quizKey).child("creator").value.toString()
                        if (checkQuizAuth(creator)){
                            pertanyaan = dataSnapshot.child(quizKey).child("pertanyaan").value.toString()
                            foundQuiz(true)
                        }
                        else{
                            Toast.makeText(activity,"Maaf anda tidak memilik akses untuk key ini",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        foundQuiz(false)
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
        }else{
            quizKey = et_key.getOrShowError(" Key tidak boleh kosong")
        }
    }

    private fun foundQuiz(b: Boolean) {
        Log.d(TAG,"foundQuiz . b: $b")
        when {
            !b -> { generateQuiz()
            }
            else -> {
                layoutSwitch(quiz_controller)
            }
        }
    }


    private fun writeNewQuiz(){
        val soal = Soal(pertanyaan = pertanyaan,a = jawabanA,b = jawabanB,c = jawabanC,d = jawabanD,jawaban_tepat = jawabanTepat,status = "waiting",creator = userid_key_new)
        Log.d(TAG,"writeQuiz . soal : $soal")
        quizRef.child(quizKey).setValue(soal)
        layoutSwitch(quiz_controller)
    }

    private fun layoutSwitch(v: View){
        tv_pertanyaan.text = pertanyaan
        quizControlRef =database.getReference("quiz").child(quizKey)

        if (v == quiz_creator_layout){
            quiz_creator_layout.visibility = View.VISIBLE
            quiz_controller.visibility = View.GONE
        }else{
            quiz_creator_layout.visibility = View.GONE
            quiz_controller.visibility = View.VISIBLE

        }
    }

    private fun resultLayoutSwitch(){
        getWinner()
        if (tv_timer.visibility == View.VISIBLE){
            tv_timer.visibility = View.GONE
            winner_layout.visibility = View.VISIBLE
        }else{
            tv_timer.visibility = View.VISIBLE
            winner_layout.visibility = View.GONE
        }
    }

    private fun jawabanFieldCheck(v: EditText, buttonView: CompoundButton){
        if (v.text.trim().isEmpty()){
            v.error = "${v.hint} Tidak Boleh Kosong"
            buttonView.isChecked = false
        }else if (!buttonView.isChecked){
            jawabanTepat = ""
        }else{
            when(buttonView.id){
                R.id.cb_jawabanA -> jawabanGenerate(et_jawabanA)
                R.id.cb_jawabanB -> jawabanGenerate(et_jawabanB)
                R.id.cb_jawabanC -> jawabanGenerate(et_jawabanC)
                R.id.cb_jawabanD -> jawabanGenerate(et_jawabanD)
            }
            checkBoxSwitch(buttonView)
        }
    }

    private fun jawabanGenerate(editText: EditText?) {
        if (editText?.text?.toString()?.trim()!!.isEmpty()){
            editText.error = "${editText.hint} Tidak Boleh Kosong"
        }else{
            jawabanTepat = editText.text.toString()
        }
    }

    private fun EditText.getOrShowError(errorMsg: String): String {
        val input = this.text.toString().trim()
        return if (input.isEmpty()||equals("")) {
            "".also {
                this.error = errorMsg
            }
        } else {
            input
        }
    }

    private fun checkBoxSwitch(buttonView: CompoundButton){
        if (viewX != buttonView){
            y = x
            viewY = view?.findViewById(y)
            viewY?.isChecked = false
            x = buttonView.id
            viewX = view?.findViewById(x)
        }else{
            x = buttonView.id
            viewX = view?.findViewById(x)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when(buttonView.id){
            R.id.cb_jawabanA -> if (isChecked)jawabanFieldCheck(et_jawabanA,buttonView)
            R.id.cb_jawabanB -> if (isChecked)jawabanFieldCheck(et_jawabanB,buttonView)
            R.id.cb_jawabanC -> if (isChecked)jawabanFieldCheck(et_jawabanC,buttonView)
            R.id.cb_jawabanD -> if (isChecked)jawabanFieldCheck(et_jawabanD,buttonView)
        }
    }

    private fun stopQuiz() {
        timer.cancel()
        quizControlRef.child("status").setValue("waiting")
    }

    private fun startQuiz() {
        timer.start()
        quizControlRef.child("status").setValue("start")
    }

    private val timer = object: CountDownTimer(30000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            tv_timer.text = (millisUntilFinished / 1000).toString()
        }
        override fun onFinish() {

        }
    }

    private fun getWinner(){
        quizWinnerRef =database.getReference("winner").child(quizKey)
        quizWinnerRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {

            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value == null){
                    tv_name.text = "Pemenang Belum Ada"
                }else{
                    val winner = dataSnapshot.child(quizKey).value.toString()
                    tv_name.text = winner
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_buat -> checkSoalKeyAlreadyOrNot()
            R.id.btn_mulai -> startQuiz()
            R.id.btn_stop -> stopQuiz()
            R.id.btn_hasil -> resultLayoutSwitch()
        }
    }

    private fun getUserIdLocal() {
        val sharedPreferences: SharedPreferences = context!!.getSharedPreferences(USERID_KEY, Context.MODE_PRIVATE)
        userid_key_new = sharedPreferences.getString(userid_key, "").toString()
        Log.d(TAG,"UserId is : $userid_key_new")
    }


}
