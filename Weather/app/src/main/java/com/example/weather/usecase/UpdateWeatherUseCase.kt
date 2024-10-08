package com.example.weather.usecase

import android.util.Log
import com.example.weather.data.openMeteoAPI.APIRepo
import com.example.weather.data.room.Weather
import com.example.weather.data.room.WeatherRepo
import com.example.weather.entity.ApiErrorResponse
import com.example.weather.entity.ApiResponseBodyEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class UpdateWeatherUseCase @Inject constructor(
    private val api: APIRepo,
    private val weatherRepo: WeatherRepo
) {
    suspend fun execute(
        cityId: String,
        lat: Double,
        lon: Double,
        failureCallback: (String) -> Unit
    ) {
        val response = api.getResponse(lat, lon)
        //интерфейс ApiResponseBodyEntity декларирует что ответ апи isSuccessful, и body !=null
        if (response is ApiResponseBodyEntity) {

            //удаляем устаревшую запись, если она есть
            if (weatherRepo.exists(cityId)) weatherRepo.deleteWeather(cityId)

            //запись погоды в настоящий момент
            val currentDT = LocalDateTime.parse(response.current.time, formatter)
            weatherRepo.insertWeather(
                Weather(
                    cityId = cityId,
                    data = currentDT.dayOfWeek.name.lowercase(),
                    temperature = response.current.temp,
                    time = currentDT.toLocalTime().toString()
                )
            )
            //в цикле записываем почасовой прогноз:
            //из списка "time": ["2022-07-01T00:00","2022-07-01T01:00", ...]
            //и списка "temp": [13.7,13.3,12.8,12.3,11.8, ...]
            //берем одинаковые индексы (если нужен не каждый час, то указываем соответствующий step)
            for (i in response.hourly.time.indices step 4) {
                val hourlyDataTime = LocalDateTime.parse(response.hourly.time[i], formatter)
                if (hourlyDataTime.isAfter(currentDT)) {
                    try {
                        weatherRepo.insertWeather(
                            Weather(
                                cityId = cityId,
                                data = hourlyDataTime.dayOfWeek.name.lowercase(),
                                temperature = response.hourly.temp[i],
                                time = hourlyDataTime.toLocalTime().toString()
                            )
                        )
                    } catch (exception: IndexOutOfBoundsException) {
                        Log.d("MyTag", "i in hourlyTimeIndexOutOfBoundsException")
                        break
                    }
                    if (hourlyDataTime.isAfter(currentDT.plusDays(4))) break
                }
            }
        //если апи вернул объект ApiErrorResponse, то в его поле message, ожидается описание ошибки,
        //которое можно вернуть в UI слой и как-то использовать с помощью failureCallback
        } else if (response is ApiErrorResponse) failureCallback(response.message)
    }

    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
    }
}