package com.`as`.interview.handler

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.util.LogPrinter
import android.util.Printer
import android.view.View
import com.`as`.interview.R

class Handler2Activity : AppCompatActivity() {
    lateinit var handler1: Handler
    lateinit var handler2: Handler
    lateinit var handler3: Handler
    lateinit var handler4: Handler
    lateinit var handler5: Handler
    lateinit var handler6: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handler2)
        handler1 = Handler()
        handler2 = Handler(Looper.myLooper()!!, object : Handler.Callback {
            override fun handleMessage(msg: Message): Boolean {
                Log.e("xxx", "handler2 get callback msg.what= ${msg.what}")
                return false
            }
        })

        handler3 = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                Log.e("xxx", "handler3 override handleMessage msg.arg1= ${msg.arg1}")
            }
        }


        Log.e("xxx", "\n ${handler1.looper} \n ${handler1.looper} \n ${handler1.looper}")

        Thread {
            handler1.post {
                Log.e("xxx", "handler1.post in ${Thread.currentThread()}")
            }
            handler2.sendEmptyMessage(1)

            val msg = Message.obtain()
            msg.arg1 = 1
            handler3.sendMessage(msg)
        }.start()

        Thread {
            Looper.prepare()
//            handler4 = Handler()
            handler4 = Handler(Looper.myLooper()!!, object : Handler.Callback {
                override fun handleMessage(msg: Message): Boolean {
                    Log.e(
                        "xxx",
                        "handler4.handleMessage in ${Thread.currentThread()}  : ${msg.what}"
                    )
                    return false
                }
            })

            handler4.post {
                Log.e("xxx", "handler4.post in ${Thread.currentThread()}")
            }
            Looper.loop()//--> for.. msg = queue.next() --> msg.target.dispatchMessage(msg)

            handler4.sendEmptyMessage(2)/*收不到，因为loop会一直循环，代码无法向下走*/
        }.start()

        val handlerThread = HandlerThread("thread-child")
        handlerThread.start()
//        handlerThread.looper.setMessageLogging(LogPrinter(Log.ERROR,"xxx"))

        handler5= Handler(handlerThread.looper,object :Handler.Callback{
            override fun handleMessage(msg: Message): Boolean {
                Log.e("xxx","handler5.handleMessage in ${Thread.currentThread()}  : ${msg.what}")
                return false
            }
        })

        handler5.sendEmptyMessage(2)

        Thread{
            handler5.post {
                Log.e("xxx", "handler5.post in ${Thread.currentThread()}")
            }
            handler5.sendEmptyMessage(4)

            handler6=Handler(Looper.getMainLooper(),object :Handler.Callback{
                override fun handleMessage(msg: Message): Boolean {
                    Log.e("xxx", "handler6.handleMessage in ${Thread.currentThread()} ${msg.what}")
                    return false
                }
            })
            handler6.post {
                Log.e("xxx", "handler6.post in ${Thread.currentThread()}")
            }
            handler5.post {
                handler6.sendEmptyMessage(6)
            }
        }.start()

    }
}