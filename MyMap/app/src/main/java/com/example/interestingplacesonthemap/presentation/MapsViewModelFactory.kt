package com.example.interestingplacesonthemap.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class MapsViewModelFactory @Inject constructor(
    private val mapsViewModel: MapsViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return mapsViewModel as T
    }
}