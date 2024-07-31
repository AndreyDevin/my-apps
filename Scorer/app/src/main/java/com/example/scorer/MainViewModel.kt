package com.example.scorer

import android.os.Build
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
    var stateInitDialog = MutableStateFlow(false)
    val uiDataFlow = MutableStateFlow<List<UiDataObject>?>(null)

    init {
        viewModelScope.launch {
            allDay.collect {
                if (it.isNotEmpty()) uiDataFlow.value = createDataObjectsForUI()
            }
        }
        viewModelScope.launch {
            stateInitDialog.value = !timer.sharedPrefIsEmpty()
        }
    }

    private fun createDataObjectsForUI(): List<UiDataObject> {

        var itemDay = LocalDate.now()
        var weekCount = 0
        var weeksList = mutableListOf(mutableMapOf<Long, Long>())
        val dayKey = { itemDay.year * 1000 + itemDay.dayOfYear.toLong() }
        var monthsList: MutableList<Pair<String, Long>> = mutableListOf()
        val returnList: MutableList<UiDataObject> = mutableListOf()

        while (itemDay.isAfter(LocalDate.of(2022, 12, 31))) {
            allDay.value
                .find { it.dayKey == dayKey() }
                .also {
                    if (it != null) {
                        weeksList[weekCount][dayKey()] = (it.durations ?: 0)
                        monthsList.add(LocalDate.parse(it.date.toString()).month.name to (it.durations ?: 0))
                    }
                    else weeksList[weekCount][dayKey()] = 0
                }
            if (itemDay.dayOfWeek.value == 1) {
                weekCount++
                weeksList.add(mutableMapOf())
            }
            itemDay.minusDays(1).year.also { it ->
                if (itemDay.year != it) {
                    returnList.add(
                        UiDataObject(
                            year = itemDay.year.toString(),
                            weekList = weeksList,
                            monthList = monthsList
                                .groupBy({ it.first }, { it.second })
                                .mapValues { (_, v) -> v.sum() }
                        )
                    )
                    monthsList = mutableListOf()
                    weekCount = 0
                    weeksList = mutableListOf(mutableMapOf())
                }
            }

            itemDay = itemDay.minusDays(1)
        }
        return returnList
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