package com.example.interestingplacesonthemap.di

import com.example.interestingplacesonthemap.presentation.MapsViewModel
import com.example.interestingplacesonthemap.presentation.MapsViewModelFactory
import com.example.interestingplacesonthemap.useCase.GetMarkerList
import com.example.interestingplacesonthemap.useCase.GetPathToPoint
import dagger.Module
import dagger.Provides

@Module
class PresentationModule {

    @Provides
    fun provideMapsViewModelFactory(mapsViewModel: MapsViewModel): MapsViewModelFactory {
        return MapsViewModelFactory(mapsViewModel = mapsViewModel)
    }

    @Provides
    fun provideMapsViewModel(
        openMapsRepo: GetMarkerList,
        pathToPointUseCase: GetPathToPoint
    ): MapsViewModel {
        return MapsViewModel(
            openMapsRepo = openMapsRepo,
            pathToPointUseCase = pathToPointUseCase
        )
    }
}