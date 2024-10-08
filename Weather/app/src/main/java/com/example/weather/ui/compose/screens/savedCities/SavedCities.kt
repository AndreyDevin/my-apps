package com.example.weather.ui.compose.screens.savedCities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather.ui.viewmodels.SavedCitiesViewModel

@Composable
fun SavedCities(
    onCityClick: (String) -> Unit,
    viewModel: SavedCitiesViewModel = hiltViewModel(),
) {
    val state by viewModel.allCity.collectAsStateWithLifecycle()

    Column {

        Text(
            text = "Saved cities:",
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (state.isNotEmpty()) state.forEach {
            Text(
                modifier = Modifier.clickable { onCityClick(it.cityId) },
                text = it.name
            )
        }
    }
}