package com.example.composablerickmorty.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.composablerickmorty.data.Repository
import com.example.composablerickmorty.dto.Character
import com.example.composablerickmorty.dto.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository): ViewModel() {

    val state: MutableStateFlow<UiData> = MutableStateFlow(UiData.InitState)

    private val backStack = mutableListOf<UiData>()

    private val characterPagingData: Flow<PagingData<Character>> =
        repository.getCharacterPagingDataFlow()
            .cachedIn(viewModelScope)

    private val locationPagingData: Flow<PagingData<Location>> =
        repository.getLocationsPagingDataFlow()
            .cachedIn(viewModelScope)

    fun shiftUiStateToCharacter(id: Int) {
        viewModelScope.launch {
            UiData.SingleCharacter(repository.getSingleCharacter(id)).also {
                state.value = it
                backStack.add(it)
            }
        }
    }

    fun shiftUiStateToCharactersList() {
        UiData.CharacterPagingData(characterPagingData).also {
            state.value = it
            backStack.add(it)
        }
    }

    fun shiftUiStateToLocationsList() {
        UiData.LocationPagingData(locationPagingData).also {
            state.value = it
            backStack.add(it)
        }
    }

    fun onBackPressed() {
        backStack.removeLastOrNull() ?: return
        state.value = backStack.lastOrNull() ?: return
    }
}

sealed class UiData {
    data object InitState: UiData()
    data class CharacterPagingData(val characterPagingData: Flow<PagingData<Character>>): UiData()
    data class LocationPagingData(val locationPagingData: Flow<PagingData<Location>>): UiData()
    data class SingleCharacter(val singleCharacter: Character): UiData()
}