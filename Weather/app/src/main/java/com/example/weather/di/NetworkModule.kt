package com.example.weather.di

import com.example.weather.data.openMeteoAPI.OpenMeteoAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideYandexAPI(): OpenMeteoAPI {
        return OpenMeteoAPI.create()
    }
}