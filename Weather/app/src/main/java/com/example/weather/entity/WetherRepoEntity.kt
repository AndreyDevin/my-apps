package com.example.weather.entity

interface WeatherRepoEntity {
    suspend fun getWeather(cityId: String): CityWithWeatherEntity

    suspend fun insertWeather(weather: WeatherEntity)

    suspend fun deleteWeather(cityId: String)

    suspend fun exists(cityId: String): Boolean
}