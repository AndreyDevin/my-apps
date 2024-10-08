package com.example.weather.usecase

import com.example.weather.data.room.CityRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteCityUseCase @Inject constructor(private val cityRepo: CityRepo) {

    fun execute(cityId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            cityRepo.deleteCity(cityId)
        }
    }
}