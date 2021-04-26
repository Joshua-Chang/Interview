package com.`as`.interview

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.`as`.interview.handler.HandlerActivity
import com.`as`.interview.service.ServiceActivity

class MainActivity : AppCompatActivity() {
    val TAG:String="MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG,"onCreate")
        setContentView(R.layout.activity_main)
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

    fun handler(view: View) {
        startActivity(Intent(this, HandlerActivity::class.java))
        intent.setClassName("","")
    }

    fun service(view: View) {
        startActivity(Intent(this, ServiceActivity::class.java))
    }

    fun activity(view: View) {
        val packageName = "com.as.app2"
        val fullClassName = "com.as.app2.HomeActivity"
        /*显式Intent：设置ComponentName的Intent。setComponent()、setClass()、setClassName()，或 Intent 构造函数设置组件名称*/
//        startActivity(Intent().apply {
//            component= ComponentName(packageName,fullClassName)
//            setClass(this@MainActivity/*表示不是这个扩展函数apply的this*/,ServiceActivity::class.java)
//            setClassName(packageName,fullClassName)
//        })

//        startActivity(Intent().apply {
//            component=ComponentName(packageName,fullClassName)
//        })


//        startActivity(Intent().apply {
//            action = Intent.ACTION_VIEW
//            data= Uri.parse("xl://goods:8888/goodsDetail?goodsId=10011002")
//        })

//


        val intent= Intent("com.as.interview.app_test")
//            .apply {
//            `package`=packageName
//        }
        if (intent.resolveActivity(packageManager) != null) {/*验证至少有一个能接收该intent的activity*/
            startActivity(intent)
        }

//        val queryIntentActivities =
//            packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN), MATCH_ALL)
//
//        queryIntentActivities.forEach {
//            Log.e("xxx",it.activityInfo.name)
//        }

        /*"geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California"*/
//        val callIntent: Intent = Uri.parse("tel:5551234").let { number ->
//            Intent(Intent.ACTION_DIAL, number)
//        }
//        val it=Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com/training/basics/intents/sending?hl=zh-cn"))
//        val it=Intent(Intent.ACTION_SEND).apply {
//            type="text/plain"
//            putExtra(Intent.EXTRA_EMAIL, arrayOf("jan@example.com")) // recipients
//            putExtra(Intent.EXTRA_SUBJECT, "Email subject")
//            putExtra(Intent.EXTRA_TEXT, "Email message text")
//            putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment"))
//        }

        /*强制创建应用选择器*/
//        val it=Intent(Intent.ACTION_SEND)
//        val chooser=Intent.createChooser(it,"Share this photo with")
//        startActivity(chooser)
    }
}

