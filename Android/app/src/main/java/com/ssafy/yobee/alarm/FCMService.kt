package com.ssafy.yobee.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.yobee.R
import com.ssafy.yobee.ui.MainActivity
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    lateinit var title: String
    lateinit var content: String

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.notification != null) {
            title = message.notification!!.title.toString()
            content = message.notification!!.body.toString()
        }

        sendNotification(title, content)
    }

    private fun sendNotification(title: String, content: String) {
        val fcmIntent = Intent(this, MainActivity::class.java).apply {
            putExtra(FCM_EXTRA, FCM_EXTRA)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent =
            PendingIntent.getActivity(this, 0, fcmIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_fcm)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(Random.nextInt(), builder)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = CHANNEL_NAME
        val channel = NotificationChannel(
            CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_NAME
            enableLights(true)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }

        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "YOBEE"
        private const val CHANNEL_NAME = "YOBEE FCM"
        private const val FCM_EXTRA = "recommend"
    }
}