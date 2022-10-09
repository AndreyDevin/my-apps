package com.example.m16marsgram.domain

import com.example.m16marsgram.data.AllRoversMissionsDto
import com.example.m16marsgram.data.NasaRepository
import javax.inject.Inject

class GetMissionsInfo @Inject constructor(
    private val nasaRepository: NasaRepository,
) {
    suspend fun getMissionsInfo(): AllRoversMissionsDto {
        return nasaRepository.missionsInfoApi.getMissionsInfoList()
    }
}