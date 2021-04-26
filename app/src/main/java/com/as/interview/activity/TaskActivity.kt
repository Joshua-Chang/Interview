package com.`as`.interview.activity

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.`as`.interview.R

class TaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        findViewById<TextView>(R.id.tv_task).setOnClickListener {
            val packageName = "com.as.app2"
            val fullClassName = "com.as.app2.TargetActivity2"

            startActivity(Intent().apply {
                component = ComponentName(packageName, fullClassName)
            })
        }
    }
}