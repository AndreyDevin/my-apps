package com.example.weather.entity

data class WeatherDTO(
    override val id: Int,
    override val cityId: String,
    override val data: String,
    override val time: String?,
    override val temperature: Double
): WeatherEntity
