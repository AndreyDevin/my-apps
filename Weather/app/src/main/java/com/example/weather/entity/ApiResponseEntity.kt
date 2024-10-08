package com.example.weather.entity

sealed interface ApiResponseEntity {

    data class SuccessResponse(
        override val current: ApiCurrentEntity,
        override val hourly: ApiHourlyEntity
    ) : ApiResponseBodyEntity, ApiResponseEntity

    data class ErrorResponse(override val message: String): ApiErrorResponse, ApiResponseEntity
}

interface ApiErrorResponse {
    val message: String
}

interface ApiResponseBodyEntity {
    val current: ApiCurrentEntity
    val hourly: ApiHourlyEntity
}

interface ApiCurrentEntity {
    val time: String
    val temp: Double
}

interface ApiHourlyEntity {
    val time: List<String>
    val temp: List<Double>
}