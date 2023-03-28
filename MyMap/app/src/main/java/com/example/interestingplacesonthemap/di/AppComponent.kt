package com.example.interestingplacesonthemap.di

import com.example.interestingplacesonthemap.presentation.MapsActivity
import dagger.Component

@Component
interface AppComponent {
    fun inject(mapsActivity: MapsActivity)
}