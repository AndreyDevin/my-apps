package com.example.weather.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        City::class,
        Weather::class
    ],
    version = 1, //менять в случае миграции
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun cityDao(): CityDao

    abstract fun weatherDao(): WeatherDao

    companion object {

        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                //.inMemoryDatabaseBuilder(context, AppDatabase::class.java) //у inMemoryDatabaseBuilder() нет имени
                .build()
        }

        private const val DATABASE_NAME = "skillbox_weather_db"
    }
}
