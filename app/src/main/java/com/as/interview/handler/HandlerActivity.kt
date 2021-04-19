package com.`as`.interview.handler

import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.`as`.interview.R
/**
 * Looper 负责的是创建一个 MessageQueue 对象,
 * 然后进入到一个无限循环体中不断取出消息,
 * 而这些消息都是由一个或者多个 Handler 进行创建处理*/
class HandlerActivity : AppCompatActivity() {
    var mHandler0: Handler? = null
    var mHandler1: Handler? = null
    var mHandler2: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handler)
        /* Handler 是Android消息机制的上层接口，其使用过程：将一个任务切换到 Handler 所在的线程中去执行*/
        /* 使用场景就是，子线程中,进行耗时操作,执行完操作后,发送消息,通知主线程更新 UI*/
        mHandler0 = Handler()

        Thread {
            Looper.prepare()
            // Can't create handler inside thread that has not called Looper.prepare()
            mHandler1 = Handler()
        }.start()
        Thread {
            val msg = Message.obtain()
            msg.arg1 = 1
            msg.arg2 = 2
            var bundle = Bundle()
            bundle.putChar("key", 'v')
            bundle.putString("key", "value")
            msg.data = bundle
            mHandler0!!.sendMessage(msg)
        }.start()

        /*在子线程中创建 Handler*/
        val handlerThread = HandlerThread("handlerThread1")
        handlerThread.start()
//        handlerThread.quit() 停止
        /**
         * @see HandlerThread.run  此时 looper 已经被 prepare，因此可用Handler(looper)来构造Handler*/
        mHandler2 = object :Handler(handlerThread.looper,object :Handler.Callback{
            override fun handleMessage(msg: Message): Boolean {
                Log.e("xxx","Callback ${msg.what}"+ Thread.currentThread().name)
                return false/*表示还继续回调后续的复写方法*/
            }
        }){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                Log.e("xxx","override ${msg.what}"+ Thread.currentThread().name)
            }
        }
        /*同理：像在 intentService(子线程)中,在 UI 线程回调*/
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this,"Loaded Person from broadcast-receiver->intent-service: ", Toast.LENGTH_LONG).show()
        }
    }
    fun sendEmptyMessage(view: View) {
        mHandler2?.sendEmptyMessage(123)
    }


    /**
     * 除了发送消息外,还有以下几个方法可以在子线程中进行 UI 操作
     * Handler 的 post()方法
     * View 的 post()方法
     * Activity 的 runOnUiThread()方法
     * 本质都是Handler 机制
     * */
    class MyThread : Thread() {
        var myHandler: Handler? = null
        override fun run() {
            Looper.prepare()
            /**
             * @see Handler.dispatchMessage 的分发顺序，即handleMessage的处理优先级
             */
//            myHandler = object : Handler(object : Callback {
//                override fun handleMessage(msg: Message): Boolean {
//                    TODO("Not yet implemented")
//                }
//            }) {
//                override fun handleMessage(msg: Message) {
//                    super.handleMessage(msg)
//                }
//            }
            myHandler=Handler { TODO("Callback.handleMessage") }
            myHandler=object : Handler() {
                /*Handler override handleMessage*/
            }
            Looper.loop()
        }
    }


}