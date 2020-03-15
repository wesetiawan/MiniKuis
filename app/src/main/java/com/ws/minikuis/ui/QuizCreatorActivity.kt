package com.ws.minikuis.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ws.minikuis.R
import com.ws.minikuis.fragment.QuizCreatorFragment
import kotlinx.android.synthetic.main.fragment_quiz_creator.*

class QuizCreatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_creator)
        quizCreatorFragment()
    }

    private fun quizCreatorFragment(){
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = QuizCreatorFragment()
        transaction.add(R.id.frg_holder,fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (quiz_controller.visibility == View.VISIBLE){
            quiz_creator_layout.visibility = View.VISIBLE
            quiz_controller.visibility = View.GONE
        }else super.onBackPressed()

    }
}
