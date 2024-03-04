package com.example.composablerickmorty

import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.composablerickmorty.api.RickMortyApi
import com.example.composablerickmorty.data.Repository
import com.example.composablerickmorty.ui.ViewModelFactory

object Injection {
    private fun provideRepository(): Repository {
        return Repository(RickMortyApi.create())
    }

    fun provideViewModelFactory(owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ViewModelFactory(owner, provideRepository())
    }
}