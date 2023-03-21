package com.example.interestingplacesonthemap.useCase

import com.example.interestingplacesonthemap.data.Features
import com.example.interestingplacesonthemap.data.OpenMapsRepository

class GetMarkerList {
    private val openMapsRepo = OpenMapsRepository()

    suspend fun getMarkerList(lat: Double, lon: Double, radius: Int): List<Features> {
        return openMapsRepo.placesInRadius.getPlacesInRadius(lat, lon, radius).featureCollection
    }
}