package com.`as`.interview.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class BackService : Service() {
    var mThread :Thread? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            mThread=Thread{
                while (true){
                    if (mThread!!.isInterrupted)throw InterruptedException()
                    println("onStartCommand")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mThread?.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("onBind")
        return MyBinder()
    }
    class MyBinder :Binder(){
        fun showTips(){
            println("showTips")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mThread?.interrupt()
    }
}