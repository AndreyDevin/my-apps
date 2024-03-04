package com.example.composablerickmorty.data

import androidx.paging.PagingData
import com.example.composablerickmorty.api.RickMortyApi
import com.example.composablerickmorty.dto.Character
import com.example.composablerickmorty.dto.CharactersResponse
import com.example.composablerickmorty.dto.Location
import com.example.composablerickmorty.dto.LocationResponse
import kotlinx.coroutines.flow.Flow

class Repository(
    private val api: RickMortyApi
) {
    fun getLocationsPagingDataFlow(): Flow<PagingData<Location>> =
        MainPagingSource<LocationResponse, Location>(api::getLocations).create()

    fun getCharacterPagingDataFlow(): Flow<PagingData<Character>> =
        MainPagingSource<CharactersResponse, Character>(api::getCharacters).create()

    suspend fun getSingleCharacter(id: Int): Character = api.getSingleCharacter(id)
}