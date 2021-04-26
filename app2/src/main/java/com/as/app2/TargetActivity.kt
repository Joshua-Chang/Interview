package com.`as`.app2

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
class TargetActivity : AppCompatActivity() {
    val TAG:String="TargetActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG,"onCreate")
        setContentView(R.layout.activity_target)
        if (intent != null) {
            val uri: Uri = intent.data!!
            val url = uri.toString()
            Log.e("xxx", url)
            Log.e("xxx", "\n ${uri.authority}\t${uri.scheme}\t${uri.host}\t${uri.port}\t${uri.path}\t${uri.query}\t${uri.getQueryParameter("goodsId")}\t${uri.scheme}")
            findViewById<TextView>(R.id.tv).text = "url=$uri\n finalPath=${uri.scheme}://${uri.host}:${uri.port}/${uri.path}?${uri.query}"
        }
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