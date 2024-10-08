package com.example.weather.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFERENCE_NAME = "preference_name"
private const val SHARED_PREFS_KEY = "shared_prefs_key"

@Singleton
class SharedPrefRepo @Inject constructor(private val context: Context) {

    private val prefs: SharedPreferences
        get() = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)

    fun saveString(string: String) {
        prefs.edit().putString(SHARED_PREFS_KEY, string).apply()
    }

    fun getString() = prefs.getString(SHARED_PREFS_KEY, "") ?: "null"
}