package com.example.interestingplacesonthemap

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

//для обработки сообщений от фаэрбэйс в бэкграунде (при закрытом приложении) нужны только зависимость в грэдл
//и описание маленькой иконки в манифесте, больше ничего писать не нужно
//для обработки сообщений от фаэрбэйс в фореграунде (при открытом приложении) пишем соответствующий сервис
//это класс, наследуемый от FirebaseMessagingService, как и всякий сервис, его надо зарегить в манифесте
class FcmService : FirebaseMessagingService() {
    //вызывается каждый пришедший пуш
    //это JSON объект, который сериализуется в класс RemoteMessage
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notification = NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.notifications_ic)
            .setContentTitle(message.data["nickname"])
            .setContentText(message.data["message"] + getCurrentTime())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)//автоматически удаляет уведомление после нажатия на него, иначе придётся ручками
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
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
        NotificationManagerCompat.from(this).notify(Random.nextInt(), notification)
    }

    //вызывется, когда приходит новый токен, обычно здесь сохраняют токен на сервере
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy  HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date(System.currentTimeMillis()+10_800_000))
    }
}