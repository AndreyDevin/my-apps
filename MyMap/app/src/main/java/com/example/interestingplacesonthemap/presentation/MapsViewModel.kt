package com.example.interestingplacesonthemap.presentation

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interestingplacesonthemap.data.Features
import com.example.interestingplacesonthemap.data.PathToPointDto
import com.example.interestingplacesonthemap.data.Point
import com.example.interestingplacesonthemap.useCase.GetMarkerList
import com.example.interestingplacesonthemap.useCase.GetPathToPoint
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapsViewModel @Inject constructor(
    val openMapsRepo: GetMarkerList,
    val pathToPointUseCase: GetPathToPoint,
    val speedingNotification: MyNotification
) : ViewModel() {

    val myLocation = MutableStateFlow<Location?>(null)

    private var momentLatestUpdate: Pair<Location, Long>? = null
    private val requestMoratorium: Boolean
        get() = if (myLocation.value == null || momentLatestUpdate == null) true
        else myLocation.value?.distanceTo(momentLatestUpdate!!.first)!! < speedDependentDistance()
                && momentLatestUpdate!!.second - System.currentTimeMillis() < 9300L

    var checkedMarker = MutableStateFlow<Marker?>(null)
    var radiusMarkersScope = MutableStateFlow(DEFAULT_RADIUS_MARKERS_SCOPE_METERS)

    private val _flowPlacesList = MutableStateFlow<List<Features>?>(null)
    val flowPlacesList = _flowPlacesList.asStateFlow()

    private val _flowPathToPoint = MutableStateFlow<PathToPointDto?>(null)
    val flowPathToPoint = _flowPathToPoint.asStateFlow()

    private val _notifyAboutRequestChannel = Channel<Int>()
    val notifyAboutRequestChannel = _notifyAboutRequestChannel.receiveAsFlow()

    val exceptionList = mutableListOf<String>()
    var locationCount = 0
    var markerRequestCount = 0
    var routeRequestCount = 0
    var remainingDistance = 0f

    private var reversedPathToPoint = mutableListOf<Location>()
    private val listFirstPointAndDistanceToIt = mutableListOf<Pair<Location, Float>>()

    init {
        viewModelScope.launch {
            myLocation.collect {
                it?.let {
                    speedingNotification.checkSpeedOverLimit(it.speed)
                    locationCount++
                    if (!requestMoratorium) {
                        getMarkerList()
                    }
                    routeBuilding()
                }
            }
        }

        viewModelScope.launch {
            checkedMarker.collect {
                reversedPathToPoint = mutableListOf()
                if (it == null) _flowPathToPoint.value = null
                else getPathToPoint()
            }
        }

        viewModelScope.launch {
            radiusMarkersScope.collect { getMarkerList() }
        }
    }

    private suspend fun getMarkerList() {
        if (radiusMarkersScope.value != 0 && myLocation.value != null) {
            momentLatestUpdate = myLocation.value!! to System.currentTimeMillis()
            markerRequestCount++

            _flowPlacesList.value = openMapsRepo.getMarkerList(
                myLocation.value!!.latitude,
                myLocation.value!!.longitude,
                radiusMarkersScope.value
            )
        }
    }

    private suspend fun getPathToPoint() {
        if (myLocation.value != null && checkedMarker.value != null) {
            momentLatestUpdate = myLocation.value!! to System.currentTimeMillis()
            notifyAboutRequest()
            try {
                _flowPathToPoint.value = pathToPointUseCase.getPathToPoint(
                    myLocation.value!!.latitude to myLocation.value!!.longitude,
                    checkedMarker.value!!.position.latitude to checkedMarker.value!!.position.longitude
                )
            } catch (t: Throwable) {
                exceptionList.add(t.toString())
            }
            reversedPathToPoint = mutableListOf()
            _flowPathToPoint.value!!.routes.first().legs.first().points.reversed()
                .forEach { point ->
                    reversedPathToPoint.add(Location("").also {
                        it.latitude = point.latitude
                        it.longitude = point.longitude
                    })
                }
        } else _flowPathToPoint.value = null
    }

    private suspend fun notifyAboutRequest() {
        routeRequestCount++
        _notifyAboutRequestChannel.send(routeRequestCount)
    }

    fun speedDependentDistance(): Float {
        myLocation.value?.speed.also {
            if (it != null) return it * it / 3 + 50
        }
        return 50f
    }

    //создание маршрута, по принципу: "делать новый запрос в сеть нежелательно,
    //использовать, по-возможности, ранее полученные точки маршрута"
    private suspend fun routeBuilding() {
        if (reversedPathToPoint.isNotEmpty()) {
            //в цикле ищутся и удаляются уже пройденные точки
            for (i in reversedPathToPoint.size - 1 downTo (1)) {
                if (reversedPathToPoint[i].distanceTo(reversedPathToPoint[i - 1]) + 10
                    >= myLocation.value!!.distanceTo(reversedPathToPoint[i - 1])
                ) {
                    reversedPathToPoint.removeAt(i)
                } else break
            }
            //в случае соблюдения всех условий делается вывод, что пользователь ушёл с маршрута и
            //делается запрос в сеть для уточнения данных
            if (!requestMoratorium &&
                listFirstPointAndDistanceToIt.isNotEmpty() &&
                listFirstPointAndDistanceToIt.last().first == reversedPathToPoint.last() &&
                listFirstPointAndDistanceToIt.last().second + 0.8f <
                reversedPathToPoint.last().distanceTo(myLocation.value!!)
            ) {
                getPathToPoint()
                return
            }
            //рассчитывается длина пути
            remainingDistanceCount(reversedPathToPoint)
            //в источнике данных подменяется список точек маршрута на откорректированный выше
            val correctedRoutePoints = mutableListOf<Point>()
            reversedPathToPoint.reversed().forEach {
                correctedRoutePoints.add(Point(it.latitude, it.longitude))
            }
            _flowPathToPoint.value!!.routes.first().legs.first().points = correctedRoutePoints

            listFirstPointAndDistanceToIt.add(
                reversedPathToPoint.last() to myLocation.value!!.distanceTo(reversedPathToPoint.last())
            )
        }
    }

    private fun remainingDistanceCount(reversedPathToPoint: MutableList<Location>) {
        var sum = 0f
        if (reversedPathToPoint.size > 0 && myLocation.value != null) {
            sum += reversedPathToPoint.last().distanceTo(myLocation.value!!)
        }
        if (reversedPathToPoint.size > 1) {
            for (i in reversedPathToPoint.lastIndex downTo 1) {
                sum += reversedPathToPoint[i].distanceTo(reversedPathToPoint[i - 1])
            }
        }
        remainingDistance = sum
    }

    companion object {
        private const val DEFAULT_RADIUS_MARKERS_SCOPE_METERS = 0
    }
}