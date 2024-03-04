package com.example.alarm_clock.di

import com.example.alarm_clock.ui.AlarmIntent
import com.example.alarm_clock.ui.MainViewModel
import com.example.alarm_clock.ui.NotificationAboutSetAlarmTime
import com.example.alarm_clock.use_case.GetSunriseTimeByGPS
import com.example.alarm_clock.util.LastLocationProvider
import com.example.alarm_clock.util.calculate_sunrise.GetSunriseTime
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val koinModule = module {

    single { LastLocationProvider(context = get()) }
    //single { GetTimeZone() }
    single { GetSunriseTime() }

    factory { GetSunriseTimeByGPS(
        lastLocationProvider = get(),
        //timeZoneProvider = get(),
        sunriseTimeProvider = get()) }

    single { NotificationAboutSetAlarmTime() }
    single { AlarmIntent(context = get()) }

    viewModel {
        MainViewModel(
            getSunriseTimeByGPSProvider = get(),
            notificationAboutSetAlarmTime = get(),
            alarmIntent = get()
        )
    }

    /*viewModel { params ->
        MainViewModel(
            getSunriseTimeByGPSProvider = get(),
            notificationAboutSetAlarmTime = get(),
            setAlarmTime = params.get()
        )
    }*/

}