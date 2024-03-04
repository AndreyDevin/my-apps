package com.example.alarm_clock.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.alarm_clock.App
import com.example.alarm_clock.R

private const val CHANNEL_ID: String = "id_of_channel9"

class AlarmReceiver : BroadcastReceiver() {

    private val sound: Uri =
        Uri.parse("android.resource://" + App.appContext.packageName + "/" + R.raw.ich_will_nicht)
    private val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
        .build()

    companion object {
        const val ALARM_ACTION = "W_ACTION"
        const val ALARM_INPUT_KEY = "W_INPUT"

        fun createIntent(context: Context = App.appContext, message: String): Intent {
            return Intent(context, AlarmReceiver::class.java).apply {
                action = ALARM_ACTION
                putExtra(ALARM_INPUT_KEY, message)
            }
        }
    }

    //вызывается, когда получаем извещение от системы о соответствующем интенте (намерении)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ALARM_ACTION) {
            return
        }

        val text = intent.extractInput()
        context.createNotificationChannel(CHANNEL_ID)
        context.getNotificationManager()
            .notify(text.hashCode(), createNotification(context, text))
    }

    private fun createNotification(context: Context, text: String): Notification {

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Wake up!")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true) //скроется если пользователь нажмет на него
            .build()
    }

    private fun Context.createNotificationChannel(channelId: String) {
        val name = getString(R.string.app_name)
        val descriptionText = getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = descriptionText
        channel.setSound(sound, audioAttributes)
        val notificationManager = getNotificationManager()
        notificationManager.createNotificationChannel(channel)
    }

    private fun Context.getNotificationManager(): NotificationManager {
        return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun Intent.extractInput(): String {
        return getStringExtra(ALARM_INPUT_KEY) ?: "Empty"
    }
}