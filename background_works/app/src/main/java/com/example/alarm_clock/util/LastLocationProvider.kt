package com.example.alarm_clock.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LastLocationProvider(context: Context) {

    private val _lastLocationFlow = MutableStateFlow<Location?>(null)
    val lastLocationFlow = _lastLocationFlow.asStateFlow()

    private var fusedClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                _lastLocationFlow.value = location
                //ой всё, отписка!
                onStop()
            }
        }
    }
    //чтоб работало критически важны permission.ACCESS_COARSE_LOCATION и permission.ACCESS_FINE_LOCATION
    //у меня они проверены в майнАктивити, еще до инициализации вьюМодели.
    @SuppressLint("MissingPermission")//даётся клятва, что пермишены проверены
    fun getLocation() {
        val request = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1_000)//PRIORITY_BALANCED_POWER_ACCURACY не всегда даёт результат
            .build()

        fusedClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.myLooper()
        )
    }

    //нужно отписываться, если не требуются новые координаты, ибо если приложение уйдет в бэк,
    //а requestLocationUpdates продолжит работать, попадёшь в специальное место в аду для тех, кто нет отписывается
    fun onStop() {
        fusedClient.removeLocationUpdates(locationCallback)
    }
}
//Ниже старый класс, на память.
//Его недостаток, что методом fusedLocationClient.getCurrentLocation совершается один запрос и если failure, то failure
//новый класс методом .requestLocationUpdates с лупером, будет запрашивать координаты до посинения

/*import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import androidx.core.app.ActivityCompat.checkSelfPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task

class LastLocationProvider(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private var cancellationTokenSource = CancellationTokenSource()

    fun getLocation(callback: (Result<Location>) -> Unit) {

        val currentLocationTask: Task<Location> =
            if (checkSelfPermission(context, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) return
            else fusedLocationClient.getCurrentLocation(
                PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            )

        currentLocationTask.addOnCompleteListener { task: Task<Location> ->
            val location = task.getLocation()

            if (location != null) callback.invoke(Result.success(location))
            else callback.invoke(Result.failure(NoSuchElementException("No location found")))
        }
    }

    private fun Task<Location>.getLocation(): Location? {
        return if (isSuccessful && result != null) result!!
        else null
    }
}*/