package com.example.weather.ui

import kotlinx.serialization.Serializable

sealed class Screens {
    @Serializable
    data object Search: Screens()

    @Serializable
    data object SavedCities: Screens()

    @Serializable
    data class Details(val cityId: String): Screens()
}