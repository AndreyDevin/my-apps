package com.example.weather.entity

import kotlinx.coroutines.flow.Flow

interface CityRepoEntity {
    fun getAll(): Flow<List<CityEntity>>

    suspend fun insertCity(city: CityEntity)

    suspend fun deleteCity(id: String)

    suspend fun exists(cityId: String): Boolean
}