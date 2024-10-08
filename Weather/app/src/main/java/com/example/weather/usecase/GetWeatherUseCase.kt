package com.example.weather.usecase

import com.example.weather.data.room.WeatherRepo
import com.example.weather.entity.CityDTO
import com.example.weather.entity.CityWithWeatherDTO
import com.example.weather.entity.WeatherDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(private val weatherRepo: WeatherRepo) {

    suspend fun execute(cityId: String): CityWithWeatherDTO? {
        val weather =
            CoroutineScope(Dispatchers.IO).async { weatherRepo.getWeather(cityId) }.await()

        return try {
            weather.let {
                CityWithWeatherDTO(
                    city = CityDTO(
                        cityId = it.city.cityId,
                        name = it.city.name,
                        lat = it.city.lat,
                        lon = it.city.lon
                    ),
                    weather = it.weather?.map { weather ->
                        WeatherDTO(
                            id = weather.id,
                            cityId = weather.cityId,
                            data = weather.data,
                            time = weather.time,
                            temperature = weather.temperature
                        )
                    }
                )
            }
        } catch (e: NullPointerException) { null }

    }
}