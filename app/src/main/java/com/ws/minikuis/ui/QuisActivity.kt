package com.ws.minikuis.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ws.minikuis.R
import com.ws.minikuis.fragment.QuisFragment

class QuisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quis_activity)
        openQuisFragment()
    }

    private fun openQuisFragment(){
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = QuisFragment()
        transaction.replace(R.id.frg_holder,fragment)
        transaction.commit()
    }
}
