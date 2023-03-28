package com.example.interestingplacesonthemap.data

import com.example.interestingplacesonthemap.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

// !!!  In file local.properties create property: OPEN_MAPS_API_KEY=your key
const val OPEN_MAPS_KEY = BuildConfig.OPEN_MAPS_API_KEY

const val BASE_URL = "http://api.opentripmap.com/0.1/"

class OpenMapsRepository @Inject constructor(){

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val placesInRadius: PlacesInRadius = retrofit.create(PlacesInRadius::class.java)
}

interface PlacesInRadius {
    @GET("ru/places/radius") //вернет достопримечательности в заданном радиусе от точки
    suspend fun getPlacesInRadius(
        @Query("lat") latitude: Double, //широта
        @Query("lon") longitude: Double, //долгота
        @Query("radius") radius: Int,
        @Query("apikey") apikey: String = OPEN_MAPS_KEY,
    ): OpenMapsDto
}