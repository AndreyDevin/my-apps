package com.example.weather.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weather.entity.CityEntity

@Entity
data class City(
    @PrimaryKey
    @ColumnInfo(name = "city_id")
    override val cityId: String,
    override val name: String,
    override val lat: Double,
    override val lon: Double
): CityEntity
