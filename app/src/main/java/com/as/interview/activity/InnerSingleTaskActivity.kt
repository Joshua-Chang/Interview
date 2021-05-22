package com.`as`.interview.activity

import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.`as`.interview.R

class InnerSingleTaskActivity : AppCompatActivity() {
    val TAG = "InnerSingleTaskActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate")
        setContentView(R.layout.activity_test)
        if (intent != null && intent.data != null) {
            val uri: Uri = intent.data!!
            val url = uri.toString()
            Log.e("xxx", url)
            Log.e(
                "xxx",
                "\n ${uri.authority}\t${uri.scheme}\t${uri.host}\t${uri.port}\t${uri.path}\t${uri.query}\t${
                    uri.getQueryParameter("goodsId")
                }\t${uri.scheme}"
            )
            findViewById<TextView>(R.id.tv).text =
                "url=$uri\n finalPath=${uri.scheme}://${uri.host}:${uri.port}/${uri.path}?${uri.query}"
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