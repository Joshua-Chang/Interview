package com.`as`.app2

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TargetActivity2 : AppCompatActivity() {
    val TAG = "TargetActivity2"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate")
        setContentView(R.layout.activity_target)
        findViewById<TextView>(R.id.tv).setOnClickListener {
            val packageName = "com.as.interview"
            val fullClassName = "com.as.interview.activity.TaskActivity"

            val intent = Intent().apply {
                component = ComponentName(packageName, fullClassName)
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
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