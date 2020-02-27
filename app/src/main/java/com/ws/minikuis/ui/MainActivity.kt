package com.ws.minikuis.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ws.minikuis.R
import com.ws.minikuis.fragment.MainFragment
import com.ws.minikuis.fragment.ResultFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openQuisFragment()
    }

    fun openQuisFragment(){
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = MainFragment()
        transaction.replace(R.id.frg_holder,fragment)
        transaction.commit()
    }
}
