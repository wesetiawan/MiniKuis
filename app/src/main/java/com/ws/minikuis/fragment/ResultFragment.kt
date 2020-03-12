package com.ws.minikuis.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ws.minikuis.R
import com.ws.minikuis.model.User

class ResultFragment : Fragment() {

    val TAG = "ResultFragment"
    var USERID_KEY = "useridkey"
    var userid_key = ""
    var userid_key_new = ""

    companion object {
        fun newInstance() =
            ResultFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    private fun getUserIdLocal() {
        val sharedPreferences: SharedPreferences = context!!.getSharedPreferences(USERID_KEY, Context.MODE_PRIVATE)
        userid_key_new = sharedPreferences.getString(userid_key, "").toString()
        Log.d(TAG,"UserId is : $userid_key_new")
    }

}
