package com.`as`.interview.service

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import com.`as`.interview.R

class ServiceActivity : AppCompatActivity() {
    private lateinit var mIntent: Intent
    private lateinit var connection: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)
        mIntent = Intent(this, BackService::class.java)
        connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                var myBinder = service as BackService.MyBinder
                myBinder.showTips()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                TODO("Not yet implemented")
            }
        }
    }

    fun startService(view: View) {
//        startService(mIntent)

//        startService(Intent(this,ForeService::class.java))

    }

    fun stopService(view: View) {
//        stopService(mIntent)

//        stopService(Intent(this,ForeService::class.java))//通过stopService()可以把前台服务停止
    }

    fun bindService(view: View) {
        bindService(mIntent, connection, BIND_AUTO_CREATE)
    }

    fun unbindService(view: View) {
        unbindService(connection)
    }

    fun stopForeground(view: View) {
        ForeService().stopForeground(0)
        //可以取消通知,即将前台服务降为后台服务。 此时服务依然没有停止。
    }

    fun IntentService1(view: View) {
        MyIntentService.startActionFoo(this, "foo", "from serviceActivity")
    }

    fun IntentService2(view: View) {
        MyIntentService.startActionBaz(this, "baz", "from serviceActivity")
    }
}