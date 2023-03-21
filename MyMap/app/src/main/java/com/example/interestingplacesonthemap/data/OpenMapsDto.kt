package com.example.interestingplacesonthemap.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenMapsDto(
    @Json(name = "features") val featureCollection: List<Features>,
)

@JsonClass(generateAdapter = true)
data class Features(
    @Json(name = "geometry") val geometry: Geometry,
    @Json(name = "properties") val properties: Properties,
)

@JsonClass(generateAdapter = true)
data class Geometry(
    val coordinates: List<Double>
)

@JsonClass(generateAdapter = true)
data class Properties(
    @Json(name = "xid") val xid: String,
    @Json(name = "dist") val dist: Double,
    @Json(name = "name") val name: String,
    @Json(name = "kinds") val kinds: String
)