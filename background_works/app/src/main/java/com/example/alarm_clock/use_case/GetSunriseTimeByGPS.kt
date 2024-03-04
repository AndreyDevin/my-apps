package com.example.alarm_clock.use_case

import com.example.alarm_clock.util.LastLocationProvider
import com.example.alarm_clock.util.calculate_sunrise.GetSunriseTime
import com.example.alarm_clock.util.calculate_sunrise.suncalc.SunTimes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

class GetSunriseTimeByGPS(
    private val lastLocationProvider: LastLocationProvider,
    //private val timeZoneProvider: GetTimeZone,
    private val sunriseTimeProvider: GetSunriseTime
) {
    private val _sunriseTimeFlow = MutableStateFlow<SunTimes?>(null)
    val sunriseTimeFlow = _sunriseTimeFlow.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            lastLocationProvider.lastLocationFlow.collect {location ->
                if (location != null) {
                    _sunriseTimeFlow.value = sunriseTimeProvider
                        .getSunriseTime(ZonedDateTime.now(), location.latitude, location.longitude)
                }
            }
        }
    }

    fun execute() { lastLocationProvider.getLocation() }

    //вычисление таймзоны по GPS черевато, когда географически ты в самарской зоне,
    //а на телефоне у тебя в московское время. Поэтому убрал. Использую таймзону системы.
    /*private fun getTimeZone(location: Location): ZonedDateTime {
        val tz = timeZoneProvider.iconv.getTimeZone(location.latitude, location.longitude)
        Log.d("myTag", "fun getTimeZone: $location, ${tz.displayName}" )
        return ZonedDateTime.of(LocalDateTime.now(), tz.toZoneId())
    }*/
}