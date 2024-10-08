package com.example.weather.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM city")
    fun getAll(): Flow<List<City>>

    @Insert(
        entity = City::class,
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertCity(city: City)

    @Query("DELETE FROM city WHERE city_id = :cityId")
    suspend fun deleteCity(cityId: String)

    @Query("SELECT COUNT(*) FROM city WHERE city_id = :cityId")
    suspend fun exists(cityId: String): Boolean
}