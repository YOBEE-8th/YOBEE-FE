package com.ssafy.yobee.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.text.SimpleDateFormat

class AlarmFunctions(private val context: Context) {
    fun callAlarm(triggerTime: Int, alarmCode: Int, content: String) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val receiverIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("requestCode", alarmCode)
            putExtra("content", content)
        }
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                context,
                alarmCode,
                receiverIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context,
                alarmCode,
                receiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        }
        val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm")
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + triggerTime,
            pendingIntent
        )
    }

}