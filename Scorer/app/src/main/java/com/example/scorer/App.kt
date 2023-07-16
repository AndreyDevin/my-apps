package com.example.scorer

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.scorer.data.AppDatabase

class App : Application() {

    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "db2"
        ).build()
    }
    companion object {
        lateinit var appContext: Context
    }

}