package com.example.weather.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.usecase.GetAllSavedCitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedCitiesViewModel @Inject constructor(
    getAllSavedCitiesUseCase: GetAllSavedCitiesUseCase
): ViewModel() {

    val allCity = getAllSavedCitiesUseCase.execute(viewModelScope)
}