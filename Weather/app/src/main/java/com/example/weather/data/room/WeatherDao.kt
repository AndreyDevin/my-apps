package com.example.weather.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Query("SELECT * FROM city WHERE city_id =:cityId")
    suspend fun getWeather(cityId: String): CityWithWeather

    @Insert(
        entity = Weather::class,
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertWeather(weather: Weather)

    @Query("DELETE FROM weather WHERE city_id = :cityId")
    suspend fun deleteWeather(cityId: String)

    @Query("SELECT COUNT(*) FROM weather WHERE city_id = :cityId")
    suspend fun exists(cityId: String): Boolean
}