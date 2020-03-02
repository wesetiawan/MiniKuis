package com.ws.minikuis.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ws.minikuis.R
import com.ws.minikuis.fragment.QuesFragment

class QuesActivity : AppCompatActivity() {

    var TAG = "QuestActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quis_activity)
        openQuisFragment()
    }

    private fun openQuisFragment(){
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = QuesFragment()
        transaction.replace(R.id.frg_holder,fragment)
        transaction.commit()
    }

}
