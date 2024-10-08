package com.example.weather.data.room

import com.example.weather.entity.CityEntity
import com.example.weather.entity.CityRepoEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityRepo @Inject constructor(private val cityDao: CityDao): CityRepoEntity {

    override fun getAll() = cityDao.getAll()

    override suspend fun insertCity(city: CityEntity) = cityDao.insertCity(city as City)

    override suspend fun deleteCity(id: String) = cityDao.deleteCity(id)

    override suspend fun exists(cityId: String): Boolean = cityDao.exists(cityId)
}