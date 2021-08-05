package com.as.interview.concurent;

import android.util.Log;

public class ConcurrentTest {
    Object object = new Object();
    Thread t1 = new Thread(new Runnable() {
        @Override
        public void run() {
            Log.e("xxx", "t1 run");
            synchronized (object) {
                object.notify();
            }
            Log.e("xxx", "t1 end");
        }
    });

    class T2 extends Thread {
        @Override
        public void run() {
            super.run();
            Log.e("xxx", "T2 run");
            synchronized (object) {
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("xxx", "T2 end");
            }
        }
    }

    class R implements Runnable {
        @Override
        public void run() {
            Log.e("xxx", "R1 run");
            synchronized (object) {
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("xxx", "R1 end");
            }
        }
    }

    public void test() {
        Thread t0 = new Thread(new R());
        t0.start();
        t1.start();
    }
}
