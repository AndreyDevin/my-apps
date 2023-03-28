package com.example.interestingplacesonthemap.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.interestingplacesonthemap.App
import com.example.interestingplacesonthemap.R
import javax.inject.Inject

class MyNotification @Inject constructor() {
    private val context: Context = App.appContext

    private var stateSpeedOverLimit = false

    fun checkSpeedOverLimit(speed: Float) {
        if (speed > DEFAULT_SPEED_LIMIT_MIN_PER_SEC) {
            if (!stateSpeedOverLimit) {
                createNotification()
                stateSpeedOverLimit = true
            }
        } else if (stateSpeedOverLimit) stateSpeedOverLimit = false
    }

    private fun createNotification() {
        val notification = NotificationCompat.Builder(context, App.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.notifications_ic)
            .setContentTitle("Speed over limit!")
            .setContentText(">75km/h")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 1000
        private const val DEFAULT_SPEED_LIMIT_MIN_PER_SEC = 21
    }
}