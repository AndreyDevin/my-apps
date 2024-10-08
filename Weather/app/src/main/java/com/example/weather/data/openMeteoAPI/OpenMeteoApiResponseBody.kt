package com.example.weather.data.openMeteoAPI

import com.example.weather.entity.ApiCurrentEntity
import com.example.weather.entity.ApiHourlyEntity
import com.example.weather.entity.ApiResponseBodyEntity
import com.google.gson.annotations.SerializedName

data class OpenMeteoApiResponseBody(
    override val current: Current,
    override val hourly: Hourly
): ApiResponseBodyEntity

data class Current(
    override val time: String,
    @field:SerializedName("temperature_2m") override val temp: Double
): ApiCurrentEntity

data class Hourly(
    override val time: List<String>,
    @field:SerializedName("temperature_2m") override val temp: List<Double>
): ApiHourlyEntity