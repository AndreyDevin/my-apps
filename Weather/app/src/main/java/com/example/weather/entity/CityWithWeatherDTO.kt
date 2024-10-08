package com.example.weather.entity

data class CityWithWeatherDTO(
    override val city: CityDTO,
    override val weather: List<WeatherDTO>?
): CityWithWeatherEntity
