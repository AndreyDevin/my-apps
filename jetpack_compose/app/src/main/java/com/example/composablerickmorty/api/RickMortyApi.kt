package com.example.composablerickmorty.api

import com.example.composablerickmorty.dto.Character
import com.example.composablerickmorty.dto.CharactersResponse
import com.example.composablerickmorty.dto.LocationResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickMortyApi {

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int?
    ): CharactersResponse

    @GET("character/{id}")
    suspend fun getSingleCharacter(
        @Path("id") id: Int
    ): Character

    @GET("location")
    suspend fun getLocations(
        @Query("page") page: Int?
    ): LocationResponse

    companion object {
        private const val BASE_URL = "https://rickandmortyapi.com/api/"

        fun create(): RickMortyApi {
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
                .create(RickMortyApi::class.java)
        }
    }
}