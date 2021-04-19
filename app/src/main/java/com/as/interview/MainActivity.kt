package com.`as`.interview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.`as`.interview.handler.HandlerActivity
import com.`as`.interview.service.BackService
import com.`as`.interview.service.ServiceActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun handler(view: View) {
        startActivity(Intent(this,HandlerActivity::class.java))
    }

    fun service(view: View) {
        startActivity(Intent(this,ServiceActivity::class.java))
    }
}