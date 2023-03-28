package com.example.interestingplacesonthemap.di

import com.example.interestingplacesonthemap.data.OpenMapsRepository
import com.example.interestingplacesonthemap.data.TomTomApi
import com.example.interestingplacesonthemap.useCase.GetMarkerList
import com.example.interestingplacesonthemap.useCase.GetPathToPoint
import dagger.Module
import dagger.Provides

@Module
class UseCaseModule {

    @Provides
    fun provideGetMarkerList(openMapsRepo: OpenMapsRepository): GetMarkerList {
        return GetMarkerList(openMapsRepo)
    }

    @Provides
    fun provideGetPathToPoint(tomTomApi: TomTomApi): GetPathToPoint {
        return GetPathToPoint(tomTomApi)
    }
}