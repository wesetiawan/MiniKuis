package com.ws.minikuis.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ws.minikuis.R
import com.ws.minikuis.fragment.QuizFragment
import kotlinx.android.synthetic.main.quiz_activity.*

class QuizActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    var TAG = "QuestActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_activity)
        openQuizFragment()

        refresh_layout.setOnRefreshListener(this)
    }

    private fun openQuizFragment(){
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = QuizFragment()
        transaction.replace(R.id.frg_holder,fragment)
        transaction.commit()
    }

    private fun refreshQuizFragment(){
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frg_holder)
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.detach(currentFragment!!)
        fragmentTransaction.attach(currentFragment)
        fragmentTransaction.commit()
        refresh_layout.isRefreshing=false
    }

    override fun onRefresh() {
        refresh_layout.isRefreshing=true
        refreshQuizFragment()
    }


}
