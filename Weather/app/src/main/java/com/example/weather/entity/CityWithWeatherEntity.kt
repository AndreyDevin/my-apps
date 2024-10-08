package com.example.weather.entity

interface CityWithWeatherEntity {
    val city: CityEntity
    val weather: List<WeatherEntity>?
}