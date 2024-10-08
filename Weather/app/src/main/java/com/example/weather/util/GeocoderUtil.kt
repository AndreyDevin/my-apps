package com.example.weather.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeocoderUtil @Inject constructor(@ApplicationContext context: Context) {

    private val geocoder = Geocoder(context, Locale.getDefault())

    fun getLocation(searchText: String): List<Address> {
        return try {
            geocoder.getFromLocationName(searchText, 5) ?: emptyList()
        } catch (e: IOException) {
            Log.d("MyTag", "${e.message} (maybe no internet)")
            emptyList()
        }
    }
}