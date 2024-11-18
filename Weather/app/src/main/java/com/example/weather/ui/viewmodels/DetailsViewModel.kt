package com.example.weather.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.entity.CityWithWeatherDTO
import com.example.weather.usecase.DeleteCityUseCase
import com.example.weather.usecase.DeleteWeatherUseCase
import com.example.weather.usecase.GetWeatherUseCase
import com.example.weather.usecase.UpdateWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val updateWeatherUseCase: UpdateWeatherUseCase,
    private val deleteCityUseCase: DeleteCityUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val deleteWeatherUseCase: DeleteWeatherUseCase
): ViewModel() {

    private val _cityWithWeather = MutableStateFlow<CityWithWeatherDTO?>(null)
    val cityWithWeather = _cityWithWeather.asStateFlow()

    fun getWeather(cityId: String) {
        viewModelScope.launch {
            _cityWithWeather.value = getWeatherUseCase(cityId)
        }
    }

    fun refresh(cityId: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                updateWeatherUseCase(cityId, lat, lon) {
                    //в случае ошибки апи, в лямбде можно реализовать failureCallback (не реализовано)
                    message -> Log.d("MyTag", message)
                }
                _cityWithWeather.value = getWeatherUseCase(cityId)
            }
        }
    }

    fun deleteWeather() {
            cityWithWeather.value?.also { cityWithWeather ->
                cityWithWeather.weather?.forEach { deleteWeatherUseCase(it.cityId) }
                getWeather(cityWithWeather.city.cityId)
            }
    }

    fun delCity(cityId: String) {
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                deleteCityUseCase(cityId)
                cityWithWeather.value?.weather?.forEach { deleteWeatherUseCase(it.cityId) }
            }.join()
        }
    }
}