package com.example.weather.entity

interface WeatherEntity {
    val id: Int
    val cityId: String
    val data: String
    val time: String?
    val temperature: Double
}