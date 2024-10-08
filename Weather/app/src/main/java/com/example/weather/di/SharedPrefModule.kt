package com.example.weather.di

import android.content.Context
import com.example.weather.data.SharedPrefRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SharedPrefModule {

    @Singleton
    @Provides
    fun provideSharedPref(@ApplicationContext context: Context): SharedPrefRepo {
        return SharedPrefRepo(context)
    }
}