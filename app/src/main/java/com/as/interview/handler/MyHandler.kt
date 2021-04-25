package com.`as`.interview.handler
class MyHandler(private val callBack: MyCallBack){

    fun interface MyCallBack{
        fun myHandleMessage(msg:String):Boolean
    }
    fun sendMsg(msg:String){
        callBack.myHandleMessage(msg)
    }
}