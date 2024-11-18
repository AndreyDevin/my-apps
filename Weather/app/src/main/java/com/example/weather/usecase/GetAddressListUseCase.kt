package com.example.weather.usecase

import android.location.Address
import com.example.weather.util.GeocoderUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

class GetAddressListUseCase @Inject constructor(private val geocoderUtil: GeocoderUtil) {

    suspend operator fun invoke(string: String): List<Address> {
        return CoroutineScope(Dispatchers.IO).async {
            geocoderUtil.getLocation(string)
        }.await()
    }
}