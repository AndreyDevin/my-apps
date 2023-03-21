package com.example.interestingplacesonthemap.useCase

import com.example.interestingplacesonthemap.data.PathToPointDto
import com.example.interestingplacesonthemap.data.TomTomApi

class GetPathToPoint {
    private val tomTomApi = TomTomApi()
    private var currentPath: PathToPointDto? = null

    suspend fun getPathToPoint(
        startingPoint: Pair<Double, Double>,
        endPoint: Pair<Double, Double>
    ): PathToPointDto? {
        val response = tomTomApi.pathToPoint.getPathToPoint(
            "${startingPoint.first}%2C${startingPoint.second}%3A${endPoint.first}%2C${endPoint.second}"
        )
        if (response.isSuccessful) currentPath = response.body()
        return currentPath
    }
}