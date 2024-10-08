package com.example.weather.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weather.entity.WeatherEntity

@Entity(tableName = "weather")
data class Weather(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,
    @ColumnInfo(name = "city_id")
    override val cityId: String,
    override val data: String,
    override val time: String?,
    override val temperature: Double
): WeatherEntity
