package com.example.interestingplacesonthemap.presentation

import android.location.Location
import android.util.Log
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

class MapsViewModel : ViewModel() {
    private val openMapsRepo = GetMarkerList()
    private val pathToPointUseCase = GetPathToPoint()

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

    private val _isSpeedingNotification = MutableStateFlow(false)
    val isSpeedingNotification = _isSpeedingNotification.asStateFlow()

    private val _notifyAboutRequestChannel = Channel<Int>()
    val notifyAboutRequestChannel = _notifyAboutRequestChannel.receiveAsFlow()

    val exceptionList = mutableListOf<String>()
    var locationCount = 0
    var markerRequestCount = 0
    var routeRequestCount = 0

    init {
        viewModelScope.launch {
            myLocation.collect {
                it?.let {
                    isNeedSpeedingNotification()
                    locationCount++
                    if (!requestMoratorium) { getMarkerList() }
                    routeBuilding()
                }
            }
        }

        viewModelScope.launch {
            checkedMarker.collect {
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
            try {
                momentLatestUpdate = myLocation.value!! to System.currentTimeMillis()
                notifyAboutRequest()

                _flowPathToPoint.value = pathToPointUseCase.getPathToPoint(
                    myLocation.value!!.latitude to myLocation.value!!.longitude,
                    checkedMarker.value!!.position.latitude to checkedMarker.value!!.position.longitude
                )
            } catch(t: Throwable) {
                exceptionList.add(t.toString())
            }
        } else _flowPathToPoint.value = null
    }

    private suspend fun notifyAboutRequest() {
        routeRequestCount++
        _notifyAboutRequestChannel.send(routeRequestCount)
    }

    private fun isNeedSpeedingNotification() {
        _isSpeedingNotification.value = myLocation.value!!.speed > DEFAULT_SPEED_LIMIT_MIN_PER_SEC
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
        if (flowPathToPoint.value != null) {
            val nearestLocations = mutableListOf<Location>()
            val correctedRoutePoints = mutableListOf<Point>()
            //берем несколько первых с начала маршрута точек и сохраняем их как объекты Location
            flowPathToPoint.value!!.routes.first().legs.first().points.take(4).forEach { point ->
                nearestLocations.add(Location("").also {
                    it.latitude = point.latitude
                    it.longitude = point.longitude
                })
            }
            if (!requestMoratorium) {
                //если даже ближайшая к нам точка, оказывается дальше, чем speedDependentDistance, решаем, что возможно мы ушли с маршрута и пора cделать запрос маршрута в сети
                if (nearestLocations.isEmpty() || myLocation.value?.distanceTo(nearestLocations.first())!! > speedDependentDistance()) {
                    getPathToPoint()
                    return
                }
            }
            //в новый список не будем брать точки, которые определим как уже пройденные
            nearestLocations.forEach {
                if (myLocation.value?.distanceTo(nearestLocations.last())!! > it.distanceTo(nearestLocations.last()) + 10) {
                    correctedRoutePoints.add(Point(it.latitude, it.longitude))
                }
            }
            Log.d("correctedRoutePoints", "points: ${correctedRoutePoints.size}, distanceToFirst: ${myLocation.value?.distanceTo(nearestLocations.first())!!}, distanceToLast: ${myLocation.value?.distanceTo(nearestLocations.last())!!}, dependentDistance: ${speedDependentDistance()} ")
            //откорректированный список ближайших точек складываем с остальным маршрутом
            correctedRoutePoints += flowPathToPoint.value!!.routes.first().legs.first().points.drop(4)
            _flowPathToPoint.value!!.routes.first().legs.first().points = correctedRoutePoints
        }
    }

    companion object {
        private const val DEFAULT_RADIUS_MARKERS_SCOPE_METERS = 0
        private const val DEFAULT_SPEED_LIMIT_MIN_PER_SEC = 21
    }
}