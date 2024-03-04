package com.example.composablerickmorty.ui

import androidx.compose.foundation.lazy.LazyListState

object ScrollStateHolder {
    val scrollStateMap: Map<String, LazyListState> = mapOf(
        "locations" to LazyListState(0),
        "characters" to LazyListState(0)
        )

    const val CHARACTERS_LIST_KEY = "characters"
    const val LOCATIONS_LIST_KEY = "locations"
}