package com.example.alarm_clock.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.getSystemService

class AlarmIntent(private val context: Context) {
    @SuppressLint("ScheduleExactAlarm")//пермишены проверены в MainActivity
    fun createAlarm(alarmTime: Long, clickedNumberOfMinutes: Int) {

        val alarmManager = context.getSystemService<AlarmManager>()
        val alarmType = AlarmManager.RTC_WAKEUP
        val message = createMessage(clickedNumberOfMinutes)
        Log.d("myTag","alarmIntent message = $message")
        val intent = AlarmReceiver.createIntent(context, message)

        val pendingIntent = PendingIntent.getBroadcast(//pending (в ожидании)
            context,
            1,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager?.setExactAndAllowWhileIdle(
            alarmType,
            alarmTime,
            pendingIntent
        ) ?: Toast.makeText(context, "alarmManager is null", Toast.LENGTH_LONG).show()
    }

    private fun createMessage(clickedNumber: Int): String {
        return when {
            clickedNumber < 0 -> "$clickedNumber minutes before sunrise"
            clickedNumber == 0 -> "Sunrise now!"
            else -> "$clickedNumber minutes after sunrise"
        }
    }
}