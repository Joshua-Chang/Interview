package com.`as`.app2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    val TAG ="HomeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG,"onCreate")
        setContentView(R.layout.activity_home)
    }
    override fun onRestart() {
        super.onRestart()
        Log.e(TAG,"onRestart")
    }

    override fun onStart() {
        Log.e(TAG,"onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.e(TAG,"onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.e(TAG,"onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.e(TAG,"onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.e(TAG,"onDestroy")
        super.onDestroy()
    }
}