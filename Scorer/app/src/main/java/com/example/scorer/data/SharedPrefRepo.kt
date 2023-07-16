package com.example.scorer.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.scorer.App

private const val PREFERENCE_NAME = "preference_name"
private const val SHARED_PREFS_KEY = "shared_prefs_key"

class SharedPrefRepository {

    private val context: Context = App.appContext
    private val prefs: SharedPreferences
        get() = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)

    fun saveTime(time: Long) {
        prefs.edit().putLong(SHARED_PREFS_KEY, time).apply()
    }

    fun getTime(): Long {
        return prefs.getLong(SHARED_PREFS_KEY, 0)
    }
}