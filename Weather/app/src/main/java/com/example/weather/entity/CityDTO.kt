package com.example.weather.entity

data class CityDTO(
    override val cityId: String,
    override val name: String,
    override val lat: Double,
    override val lon: Double
): CityEntity
