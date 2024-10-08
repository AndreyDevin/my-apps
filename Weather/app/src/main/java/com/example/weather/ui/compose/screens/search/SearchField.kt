package com.example.weather.ui.compose.screens.search

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(
    fieldText: State<String> = rememberSaveable { mutableStateOf("") },
    onQuery: (String) -> Unit
) {
    SearchBar(
        query = fieldText.value,
        onQueryChange = { onQuery(it) },
        onSearch = { onQuery(it) },
        active = false,
        onActiveChange = {}
    ) {}
}