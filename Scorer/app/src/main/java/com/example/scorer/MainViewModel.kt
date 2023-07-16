package com.example.scorer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scorer.data.DailyDataDao
import com.example.scorer.data.Day
import com.example.scorer.useCase.Timer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel(
    private val database: DailyDataDao,
    private val timer: Timer
) : ViewModel() {
    private val nowDayOfYear

    get() = LocalDate.now().year * 1000 + LocalDate.now().dayOfYear

    private val allDay: StateFlow<List<Day>> = this.database.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    val timerState = timer.isOn.asStateFlow()
    val destination = timer.destination.asStateFlow()
    val allWeekList = MutableStateFlow<List<Map<Long, Long>>?>(null)
    var stateInitDialog = MutableStateFlow(false)
    val listOfDurationByMonth = MutableStateFlow<List<Pair<String, Long>>?>(null)

    init {
        viewModelScope.launch {
            allDay.collect {
                if (it.isNotEmpty()) allWeekList.value = getAllWeek()

                val groupByMountList = it
                    .map { dbo -> LocalDate.parse(dbo.date.toString()) to (dbo.durations ?: 0) }
                    .groupBy { day -> day.first.month }

                listOfDurationByMonth.value = groupByMountList.map { (mount, days) ->
                    mount.name to days.sumOf { day -> day.second }
                }
                Log.d("timer_state", listOfDurationByMonth.value.toString())
            }
        }
        viewModelScope.launch {
            stateInitDialog.value = !timer.sharedPrefIsEmpty()
        }
    }

    private fun getAllWeek(): MutableList<MutableMap<Long, Long>> {
        var itemDay = LocalDate.now()
        var weekCount = 0
        val list = mutableListOf(mutableMapOf<Long, Long>())
        val deyKey = { LocalDate.now().year * 1000 + itemDay.dayOfYear.toLong() }

        while (itemDay.isAfter(LocalDate.of(2022, 12, 31))) {
            allDay.value
                .find { it.dayKey == deyKey() }
                .also {
                    if (it != null) list[weekCount][deyKey()] = (it.durations ?: 0)
                    else list[weekCount][deyKey()] = 0
                }
            if (itemDay.dayOfWeek.value == 1) {
                weekCount++
                list.add(mutableMapOf())
            }
            itemDay = itemDay.minusDays(1)
        }
        return list
    }
    fun onTimer() {
        timer.isOn.value = !timer.isOn.value

        viewModelScope.launch {
            if (timer.isOn.value) {
                if (allDay.value.isEmpty() || allDay.value.last().dayKey != nowDayOfYear.toLong()) {
                    database.insert(Day(
                        nowDayOfYear.toLong(),
                        Date(System.currentTimeMillis()),
                        null
                    ))
                }
                timer.onTimer(System.currentTimeMillis())
            }
            else database.insert(Day(
                nowDayOfYear.toLong(),
                Date(System.currentTimeMillis()),
                (allDay.value.last().durations ?: 0) + timer.destination.value
                )
            )
        }
    }

    fun updateDay(dayKey: Long, addDurations: Long) {
        viewModelScope.launch {
            database.update(dayKey, addDurations)
        }
    }

    fun deleteDay(dayKey: Long) {
        viewModelScope.launch {
            database.deleteDay(dayKey)
        }
    }

    fun insertToDataBase(dayKey: Long) {
        val date = Date(
            LocalDateTime.of(
                LocalDate.ofYearDay((dayKey/1000).toInt(), (dayKey%1000).toInt()),
                LocalTime.MIDNIGHT
            ).atZone(ZoneId.systemDefault()).toEpochSecond()*1000
        )

        viewModelScope.launch {
            database.insert(Day(
                dayKey = dayKey,
                date = date,
                durations = 1
            ))
        }
    }

    fun closeInitDialog(resumeSession: Boolean) {
        viewModelScope.launch {
            if (resumeSession) {
                stateInitDialog.value = false
                onTimer()
            }
            else {
                timer.clearTimer()
                stateInitDialog.value = !timer.sharedPrefIsEmpty()
            }
        }
    }
}
//Log.d("timer_state", timer.isOn.toString())