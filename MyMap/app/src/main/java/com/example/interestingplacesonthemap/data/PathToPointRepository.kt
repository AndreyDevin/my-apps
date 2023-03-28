package com.example.interestingplacesonthemap.data

import com.example.interestingplacesonthemap.BuildConfig
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

const val TOM_TOM_KEY = BuildConfig.TOM_TOM_API_KEY

const val TOM_TOM_API_URL = "https://api.tomtom.com/"

class TomTomApi @Inject constructor(){

    private val retrofit = Retrofit.Builder()
        .baseUrl(TOM_TOM_API_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val pathToPoint: PathToPoint = retrofit.create(PathToPoint::class.java)
}

interface PathToPoint {
    @GET("routing/1/calculateRoute/{locations}/json")
    suspend fun getPathToPoint(
        @Path(value = "locations", encoded = true) locations: String,//encoded указывает, является ли значение уже закодированным URL
        @Query("key") key: String = TOM_TOM_KEY
    ): Response<PathToPointDto>
}