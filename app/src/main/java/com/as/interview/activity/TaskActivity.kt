package com.`as`.interview.activity

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.`as`.interview.R

class TaskActivity : AppCompatActivity() {
    val TAG = "TaskActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        findViewById<TextView>(R.id.tv_standard).setOnClickListener {
            val packageName = "com.as.app2"
            val fullClassName = "com.as.app2.TargetActivity2"

            startActivity(Intent().apply {
                component = ComponentName(packageName, fullClassName)
            })
        }
        findViewById<TextView>(R.id.tv_single_task).setOnClickListener {
            val packageName = "com.as.app2"
            val fullClassName = "com.as.app2.SingleTaskActivity"

            startActivity(Intent().apply {
                component = ComponentName(packageName, fullClassName)
            })
        }
        findViewById<TextView>(R.id.tv_single_instance).setOnClickListener {
            val packageName = "com.as.app2"
            val fullClassName = "com.as.app2.SingleInstanceActivity"

            startActivity(Intent().apply {
                component = ComponentName(packageName, fullClassName)
            })
        }
        findViewById<TextView>(R.id.tv_inner_single_task).setOnClickListener {
            startActivity(Intent(this,InnerSingleTaskActivity::class.java))
        }
        findViewById<TextView>(R.id.tv_inner_single_instance).setOnClickListener {
            startActivity(Intent(this,InnerSingleInstanceActivity::class.java))
        }

    }

    override fun onRestart() {
        super.onRestart()
        Log.e(TAG, "onRestart")
    }

    override fun onStart() {
        Log.e(TAG, "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.e(TAG, "onResume")
        super.onResume()
        val ams = getSystemService(ActivityManager::class.java)
        for (task in ams.appTasks) {
            Log.e(
                TAG,
                "${task.taskInfo.numActivities}:[${task.taskInfo.baseActivity?.className}->${task.taskInfo.topActivity?.className}]"
            )
        }
    }

    override fun onPause() {
        Log.e(TAG, "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.e(TAG, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        Log.e(TAG, "onNewIntent")
        super.onNewIntent(intent)
    }
}