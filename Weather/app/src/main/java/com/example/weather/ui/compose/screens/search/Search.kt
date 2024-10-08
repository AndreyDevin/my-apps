package com.example.weather.ui.compose.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather.ui.viewmodels.SearchViewModel

@Composable
fun Search(
    onItemClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val listAddress by viewModel.listAddress.collectAsStateWithLifecycle()

    Column {

        SearchField(
            fieldText = viewModel.searchText.collectAsStateWithLifecycle(),
            onQuery = viewModel::searchAddress
        )

        if (listAddress.isNotEmpty()) {
            listAddress.forEach { address ->
                AddressItem(address = address) {
                    viewModel.getWeatherData(address, onItemClick)
                }
            }
        }
    }
}