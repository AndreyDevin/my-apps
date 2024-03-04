package com.example.alarm_clock.ui

import androidx.lifecycle.ViewModel
import com.example.alarm_clock.use_case.GetSunriseTimeByGPS
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MainViewModel(
    getSunriseTimeByGPSProvider: GetSunriseTimeByGPS,
    private val notificationAboutSetAlarmTime: NotificationAboutSetAlarmTime,
    private val alarmIntent: AlarmIntent
): ViewModel() {

    val sunriseTimeFlow = getSunriseTimeByGPSProvider.sunriseTimeFlow

    init { getSunriseTimeByGPSProvider.execute() }

    fun createAlarmTime(
        minuteCountAfterSunrise: Int,
        callback: (Result<ZonedDateTime>) -> Unit
    ) {
        if (sunriseTimeFlow.value != null) {
            if (sunriseTimeFlow.value!!.rise != null) {

                val alarmTime = sunriseTimeFlow.value!!
                    .rise!!
                    .plusMinutes(minuteCountAfterSunrise.toLong())

                notificationAboutSetAlarmTime.create(
                    text = alarmTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy, hh:mm:ss a"))
                )

                alarmIntent.createAlarm(
                    (alarmTime.toEpochSecond()) * 1000,
                    minuteCountAfterSunrise
                    )

                callback.invoke(Result.success(alarmTime))
            }
        }
    }
}
