package com.example.scorer.useCase

import com.example.scorer.data.SharedPrefRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

class Timer {
    var isOn = MutableStateFlow(false)
    var destination = MutableStateFlow(0L)
    private val sharedPref = SharedPrefRepository()

    suspend fun onTimer(time: Long) {
        val startedTime = if (sharedPref.getTime() > 0) sharedPref.getTime() else time
        sharedPref.saveTime(startedTime)
        while (isOn.value) {
            destination.value = (System.currentTimeMillis() - startedTime)/1000
            delay(1000)
        }
        sharedPref.saveTime(0)
    }

    fun clearTimer() {
        isOn.value = false
        sharedPref.saveTime(0)
    }

    fun sharedPrefIsEmpty(): Boolean {
        return sharedPref.getTime() == 0L
    }
}