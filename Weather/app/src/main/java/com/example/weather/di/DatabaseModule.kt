package com.example.weather.di

import android.content.Context
import com.example.weather.data.room.AppDatabase
import com.example.weather.data.room.CityDao
import com.example.weather.data.room.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideCityDao(appDatabase: AppDatabase): CityDao {
        return appDatabase.cityDao()
    }

    @Provides
    fun provideWeatherDao(appDatabase: AppDatabase): WeatherDao {
        return appDatabase.weatherDao()
    }
}