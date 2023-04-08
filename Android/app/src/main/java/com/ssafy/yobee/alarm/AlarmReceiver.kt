package com.ssafy.yobee.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat
import com.ssafy.yobee.R

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    companion object {
        const val CHANNEL_ID = "요리 타이머"
        const val CHANNEL_NAME = "요리 타이머"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (SDK_INT >= VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
            notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        } else {
            notificationBuilder = NotificationCompat.Builder(context)
        }
        val alarmIntent = Intent(context, AlarmService::class.java)
        val requestCode = intent?.extras!!.getInt("requestCode")
        val title = intent.extras!!.getString("content")

        val pendingIntent = if (SDK_INT >= VERSION_CODES.S) {
            PendingIntent.getActivity(
                context,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val notification = notificationBuilder.setContentTitle(title)
            .setContentText("어서 요리를 확인하세요!").setSmallIcon(R.drawable.ic_fcm)
            .setAutoCancel(true).setContentIntent(pendingIntent).build()

        notificationManager.notify(1, notification)


    }

}