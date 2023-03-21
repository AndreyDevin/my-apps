package com.example.interestingplacesonthemap.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PathToPointDto(
    @Json(name = "routes") val routes: List<Route>
)

@JsonClass(generateAdapter = true)
data class Route(
    @Json(name = "legs") val legs: List<Leg>
)

@JsonClass(generateAdapter = true)
data class Leg(
    @Json(name = "summary") val summary: Summary,
    @Json(name = "points") var points: List<Point>
)

@JsonClass(generateAdapter = true)
data class Summary(
    @Json(name = "lengthInMeters") val lengthInMeters: Int
)

@JsonClass(generateAdapter = true)
data class Point(
    @Json(name = "latitude")
    val latitude: Double,
    @Json(name = "longitude")
    val longitude: Double
)