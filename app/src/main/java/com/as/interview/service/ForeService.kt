package com.`as`.interview.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.`as`.interview.R
import com.`as`.interview.handler.HandlerActivity

class ForeService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        beginForeService()
    }

    private fun beginForeService() {
        val notificationBuilder = Notification.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher_round).setTicker("新资讯")
            .setContentText("2017-2-27")
            .setContentText("您有一条未读短信...")
            .setWhen(System.currentTimeMillis())
            .setOngoing(false)
            .setAutoCancel(true)

        val intent = Intent(this, HandlerActivity::class.java)

        val taskStackBuilder = TaskStackBuilder.create(this)
        taskStackBuilder.addParentStack(HandlerActivity::class.java)
        taskStackBuilder.addNextIntent(intent)

//        会返回到该应用程序的主界面,而不是系统的home菜单
//        val pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        notificationBuilder.setContentIntent(pendingIntent)
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = notificationBuilder.build()

        notificationManager.notify(0, notification)

        startForeground(0, notification)
    }
}