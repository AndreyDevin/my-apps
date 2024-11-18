package com.example.weather.usecase

import com.example.weather.data.room.WeatherRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteWeatherUseCase @Inject constructor(private val weatherRepo: WeatherRepo) {

    operator fun invoke(cityId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherRepo.deleteWeather(cityId)
        }
    }
}
