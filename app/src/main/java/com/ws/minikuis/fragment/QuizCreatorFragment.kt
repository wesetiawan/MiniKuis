package com.ws.minikuis.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
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
    val TAG = "QuizCreatorFragment"
    var jawabanTepat= ""
    var quizKey= ""
    var jawabanA = ""
    var jawabanB = ""
    var jawabanC = ""
    var jawabanD = ""
    var pertanyaan = ""
    private var x:Int = 0
    private var y:Int = 0
    var viewX: CheckBox? = null
    var viewY: CheckBox? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseGetInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz_creator, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_mulai.setOnClickListener(this)
        btn_kirim.setOnClickListener(this)
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
    }

    private fun generateQuiz(){
        if (et_pertanyaan.nonEmpty()&&et_jawabanA.nonEmpty()&&et_jawabanB.nonEmpty()&&et_jawabanC.nonEmpty()&&et_jawabanD.nonEmpty()&&et_key.nonEmpty() && jawabanTepat.isNotEmpty()&&jawabanTepat.isNotBlank()){
            writeQuiz()
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

    private fun checkSoalKeyAlreadyOrNot() {
        quizRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(quizKey)){
                    foundQuiz(true)
                }else{
                    foundQuiz(false)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun foundQuiz(b: Boolean) {
        when {
            !b -> { generateQuiz()
            }
            else -> { layoutSwitch(quiz_controller)
            }
        }
    }

    private fun writeQuiz(){
        val soal = Soal(pertanyaan = pertanyaan,a = jawabanA,b = jawabanB,c = jawabanC,d = jawabanD,jawaban_tepat = jawabanTepat,status = "waiting")
        quizRef.child(quizKey).setValue(soal)
        layoutSwitch(quiz_controller)

    }

    private fun layoutSwitch(v: View){
        if (v == quiz_creator_layout){
            quiz_creator_layout.visibility = View.VISIBLE
            quiz_controller.visibility = View.GONE
        }else{
            quiz_creator_layout.visibility = View.GONE
            quiz_controller.visibility = View.VISIBLE
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
        return if (input.isEmpty()) {
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

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_kirim -> checkSoalKeyAlreadyOrNot()
        }
    }

}
