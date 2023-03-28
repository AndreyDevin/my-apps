package com.example.interestingplacesonthemap

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.interestingplacesonthemap.di.AppComponent
import com.example.interestingplacesonthemap.di.DaggerAppComponent
import com.google.firebase.crashlytics.FirebaseCrashlytics

//крашлитик предполагается применять только для релизных приложений.
//чтобы ошибки на этапе разработки не попадали в фаэрбэйс и не засоряли там стату,
//он добавил строку в onCreate приложения
//раз мы создали наследника Application() его надо зарегистрировать в манифесте (там добавляется android:name=".App")
class App : Application() {
    //это для даггера
    lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.create()
        //благодаря этому, крашлитик будет отключен для дебаг версий, т.е. версий в процессе разработки
        //FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true) //чтобы все версии отправляли в крашлитик

        //для уведомления (Notification) пишем в первую очередь функцию создания канала уведомления
        //его рекомендуют создавать не в активити, а раньше, в классе приложения
        //написали  fun createNotificationChannel
        //студия подсказывает, что NotificationChannel доступен только с 8 андроида, поэтому надо добавлять проверку версии
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel()
        //способ из чата раздать контекст, сама переменная в companion object
        appContext = applicationContext
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val name = "Test notification"
        val descriptionText = "This is a simple description"
        //приоритет уведомления
        //val importance = NotificationManager.IMPORTANCE_HIGH // звук + поверх экрана
        val importance = NotificationManager.IMPORTANCE_DEFAULT // звук
        //val importance = NotificationManager.IMPORTANCE_LOW // беззвучно

        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        //используется при запуске системой приложения
        //если NotificationChannel с таким id ранее уже был создан, повторно он создаваться не будет
        const val NOTIFICATION_CHANNEL_ID = "test_channel_id"
        //используется в MyNotification.kt
        lateinit var appContext: Context
    }
}