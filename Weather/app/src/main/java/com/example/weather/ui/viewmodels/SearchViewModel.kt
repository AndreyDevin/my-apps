package com.example.weather.ui.viewmodels

import android.location.Address
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.SharedPrefRepo
import com.example.weather.usecase.GetAddressListUseCase
import com.example.weather.usecase.InsertCityUseCase
import com.example.weather.usecase.UpdateWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val sharedPrefRepo: SharedPrefRepo,
    private val getAddressListUseCase: GetAddressListUseCase,
    private val updateWeatherUseCase: UpdateWeatherUseCase,
    private val insertCityUseCase: InsertCityUseCase
): ViewModel() {

    private val _listAddress = MutableStateFlow<List<Address>>(emptyList())
    val listAddress = _listAddress.asStateFlow()

    private val _searchText = MutableStateFlow(sharedPrefRepo.getString())
    val searchText = _searchText.asStateFlow()

    fun searchAddress(text: String) {
        sharedPrefRepo.saveString(text)
        _searchText.value = sharedPrefRepo.getString()
        viewModelScope.launch(Dispatchers.IO) { _listAddress.value = getAddressListUseCase.execute(text) }
    }

    fun getWeatherData(address: Address, callback: (String) -> Unit) {
        val cityId = "${address.featureName}, ${address.subAdminArea}, ${address.adminArea}"

        //.join() можно вызвать только внутри скопа.
        //Room и API не работают на главном потоке, а переход на экран Details (callback(id))
        //наоборот, только на главном. Поэтому дожидаемся когда внутри Main отработает IO...
        viewModelScope.launch {

            CoroutineScope(Dispatchers.IO).launch {
                insertCityUseCase.execute(cityId, address.latitude, address.longitude)
                updateWeatherUseCase.execute(cityId, address.latitude, address.longitude) {
                    //в случае ошибки апи, в лямбде можно реализовать failureCallback (не реализовано)
                    message -> Log.d("MyTag", message)
                }
            }.join()

            callback(cityId)
        }
    }
}