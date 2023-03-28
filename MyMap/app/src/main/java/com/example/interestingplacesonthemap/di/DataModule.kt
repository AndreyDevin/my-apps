package com.example.interestingplacesonthemap.di

import com.example.interestingplacesonthemap.data.OpenMapsRepository
import com.example.interestingplacesonthemap.data.TomTomApi
import dagger.Module
import dagger.Provides

@Module
class DataModule {
    @Provides
    fun provideOpenMapsRepo(): OpenMapsRepository {
        return OpenMapsRepository()
    }

    @Provides
    fun provideTomTomApi(): TomTomApi {
        return TomTomApi()
    }

}