package com.example.alarm_clock.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.alarm_clock.App
import com.example.alarm_clock.R

class NotificationAboutSetAlarmTime {

    fun create(text: String){
        val notification = NotificationCompat.Builder(App.appContext, App.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("SetAlarmTime:")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) //скроется если пользователь нажмет на него
            .build()

        if (ActivityCompat.checkSelfPermission(
                App.appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        NotificationManagerCompat.from(App.appContext).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 1000
    }
}