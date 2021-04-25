//package com.as.interview.handler;
//
//import androidx.annotation.NonNull;
//
//public class MyHandler {
//
//    private final MyCallBack myCallBack;
//
//    public MyHandler(MyCallBack myCallBack) {
//        this.myCallBack = myCallBack;
//    }
//
//    public interface MyCallBack {
//        boolean myHandleMessage(@NonNull String msg);
//    }
//    public void sendMsg(String msg){
//        myCallBack.myHandleMessage(msg);
//    }
//}
