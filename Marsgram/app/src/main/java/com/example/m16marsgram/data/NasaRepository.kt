package com.example.m16marsgram.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

const val API_KEY = "qrhgvLUuMcgECvb3bhN6quwZZyrsmPbccZ98MkuD"
const val BASE_URL = "https://api.nasa.gov"

class NasaRepository @Inject constructor() {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val photosFromAllCamerasApi: PhotosFromAllCamerasApi = retrofit.create(PhotosFromAllCamerasApi::class.java)
    val photosFromDifferentCamerasApi: PhotosFromDifferentCamerasApi = retrofit.create(PhotosFromDifferentCamerasApi::class.java)
    val missionsInfoApi: MissionsInfoApi = retrofit.create(MissionsInfoApi::class.java)
}

interface PhotosFromAllCamerasApi {
    @GET("/mars-photos/api/v1/rovers/{rover}/photos")
    suspend fun getPhotos(
        @Path("rover") rover: String,
        @Query("sol") sol: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int
    ): ResponseResultDto
}

interface PhotosFromDifferentCamerasApi {
    @GET("/mars-photos/api/v1/rovers/{rover}/photos")
    suspend fun getPhotos(
        @Path("rover") rover: String,
        @Query("sol") sol: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int,
        @Query("camera") camera: String
    ): ResponseResultDto
}

interface MissionsInfoApi {
    @GET("/mars-photos/api/v1/rovers?api_key=$API_KEY")
    suspend fun getMissionsInfoList(): AllRoversMissionsDto
}

//sol	   int	    none	sol (ranges from 0 to max found in endpoint)
//camera   string	all	    see table above for abbreviations
//page	   int	    1	    25  items per page returned
//api_key  string	DEMO_KEY	api.nasa.gov key for expanded usage

//https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=1000&api_key=DEMO_KEY
//https://api.nasa.gov/mars-photos/api/v1/manifests/curiosity?api_key=qrhgvLUuMcgECvb3bhN6quwZZyrsmPbccZ98MkuD