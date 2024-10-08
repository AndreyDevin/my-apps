package com.example.weather.data.openMeteoAPI

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoAPI {

    @GET("forecast")
    suspend fun getResponse(
        @Query("latitude")lat: Double,
        @Query("longitude")lon: Double,
        @Query("current")current: String = "temperature_2m",
        @Query("hourly")hourly: String = "temperature_2m",
        @Query("timezone")timeZone: String = "auto"
    ): Response<OpenMeteoApiResponseBody>

    companion object {
        private const val BASE_URL = "https://api.open-meteo.com/v1/"

        fun create(): OpenMeteoAPI {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenMeteoAPI::class.java)
        }
    }
}