package com.example.weather.data.openMeteoAPI

import com.example.weather.entity.ApiResponseEntity
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class APIRepo @Inject constructor(private val openMeteoAPI: OpenMeteoAPI) {

    suspend fun getResponse(lat: Double, lon: Double): ApiResponseEntity {
        try {

            val response = openMeteoAPI.getResponse(lat, lon)

            return if (!response.isSuccessful) ApiResponseEntity.ErrorResponse("response code: ${response.code()}")
            else if (response.body() != null) {
                ApiResponseEntity.SuccessResponse(
                    current = response.body()!!.current,
                    hourly = response.body()!!.hourly
                )
            } else ApiResponseEntity.ErrorResponse("response body is null")


        } catch (exception: IOException) {
            return ApiResponseEntity.ErrorResponse(exception.message ?: "exception message is null")
        } catch (exception: HttpException) {
            return ApiResponseEntity.ErrorResponse(exception.message ?: "exception message is null")
        }
    }
}