package com.example.alarm_clock

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.alarm_clock.di.koinModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        //koin
        startKoin{
            androidContext(this@App)
            modules(koinModule)
        }

        //андроид рекомендует регестрировать канал уведомлений не в активити, а раньше, например здесь
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = NOTIFICATION_CHANNEL_ID
        val descriptionText = "Shows what time the alarm is set"
        val importance = NotificationManager.IMPORTANCE_HIGH // звук + поверх экрана

        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        //если NotificationChannel с таким id ранее уже был создан, повторно он создаваться не будет
        //это также означает, что если хочешь внести изменения в объект NotificationChannel,
        //то нужно изменять и id канала, таким образом появится новый канал, но и останется старый со старым id
        //например какое-то время в системе будет хранится версия с id == "notification_about_set_alarm_time2"
        //(с ихвильнихт) и к ней можно вернуться, указав этот id каналу
        const val NOTIFICATION_CHANNEL_ID = "notification_about_set_alarm_time"
        lateinit var appContext: Context
    }
}