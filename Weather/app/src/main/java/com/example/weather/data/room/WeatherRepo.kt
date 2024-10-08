package com.example.weather.data.room

import com.example.weather.entity.WeatherEntity
import com.example.weather.entity.WeatherRepoEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepo @Inject constructor(private val weatherDao: WeatherDao): WeatherRepoEntity {

    override suspend fun getWeather(cityId: String) = weatherDao.getWeather(cityId)

    override suspend fun insertWeather(weather: WeatherEntity) = weatherDao.insertWeather(weather as Weather)

    override suspend fun deleteWeather(cityId: String) = weatherDao.deleteWeather(cityId)

    override suspend fun exists(cityId: String): Boolean = weatherDao.exists(cityId)

}