package com.example.interestingplacesonthemap.useCase

import com.example.interestingplacesonthemap.data.Features
import com.example.interestingplacesonthemap.data.OpenMapsRepository
import javax.inject.Inject

class GetMarkerList @Inject constructor(val openMapsRepo: OpenMapsRepository) {
    //private val openMapsRepo = OpenMapsRepository()

    suspend fun getMarkerList(lat: Double, lon: Double, radius: Int): List<Features> {
        return openMapsRepo.placesInRadius.getPlacesInRadius(lat, lon, radius).featureCollection
    }
}