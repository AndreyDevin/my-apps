package com.example.weather.data.room

import androidx.room.Embedded
import androidx.room.Relation
import com.example.weather.entity.CityWithWeatherEntity

data class CityWithWeather(
    @Embedded
    override val city: City,
    @Relation(
        parentColumn = "city_id",
        entityColumn = "city_id",
        //entity = Weather::class //необязательно, ибо опред. авт. из возвращаемого типа
    )
    override val weather: List<Weather>?
): CityWithWeatherEntity
