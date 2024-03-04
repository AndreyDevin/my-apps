package com.example.views_and_animations.custom_analog_clock

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

class ClockUseCase(
    var updateClockView: (Int, Int, Int, Boolean, Boolean)-> Unit
) {
    private var clockIsOn = false
        set(value) {
            field = value
            stateClock.value = field
        }
    val stateClock = MutableStateFlow(clockIsOn)

    private var timePeriod: Long = 1000L
    private var isAccelerated: Boolean = false
        set(value) {
            field = value
            timePeriod = if (field) 10 else 1000
        }

    private var sec = 0
    private var min = 0
    private var hour = 0
    private var incrementTime = 0

    private var totalTimeInSeconds = 0
       set(value) {
           field = value
           hour = (field + incrementTime)/3600
           min = ((field + incrementTime) % 3600) / 60
           sec = ((field + incrementTime) % 3600) % 60
           updateClockView(sec, min, hour, clockIsOn, isAccelerated)
       }

    private suspend fun clockCycle() {
        val startedTime = System.currentTimeMillis()
        delay(timePeriod)
        while (clockIsOn) {
            totalTimeInSeconds = ((System.currentTimeMillis() - startedTime) / timePeriod).toInt()
            delay(timePeriod)
        }
    }

    suspend fun start() {
        if (!clockIsOn) {
            clockIsOn = true
            clockCycle()
        }
    }

    fun stop() {
        clockIsOn = false
        incrementTime += totalTimeInSeconds
    }

    fun reset() {
        clockIsOn = false
        incrementTime = 0
        totalTimeInSeconds = 0
    }

    fun accelerated(state: Boolean) {
        isAccelerated = state
        updateClockView(sec, min, hour, clockIsOn, isAccelerated)
    }

    fun plusHour(increment: Int) {
        incrementTime += increment*3600
        totalTimeInSeconds = totalTimeInSeconds
    }

    fun setStartedState() {
        updateClockView(sec, min, hour, clockIsOn, isAccelerated)
    }
}