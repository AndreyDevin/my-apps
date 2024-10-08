package com.example.weather.usecase

import com.example.weather.data.room.CityRepo
import com.example.weather.entity.CityDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class GetAllSavedCitiesUseCase @Inject constructor(private val cityRepo: CityRepo) {

    fun execute(scope: CoroutineScope) = cityRepo.getAll().map { listCity ->
            listCity.map { city -> CityDTO(city.cityId, city.name, city.lat, city.lon) }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
}