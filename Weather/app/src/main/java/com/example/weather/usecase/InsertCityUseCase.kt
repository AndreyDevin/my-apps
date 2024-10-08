package com.example.weather.usecase

import com.example.weather.data.room.City
import com.example.weather.data.room.CityRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class InsertCityUseCase @Inject constructor(private val cityRepo: CityRepo) {

    fun execute(id: String, latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            if (!cityRepo.exists(id)) cityRepo.insertCity(
                City(
                    cityId = id,
                    name = id,
                    lat = latitude,
                    lon = longitude
                )
            )
        }
    }
}