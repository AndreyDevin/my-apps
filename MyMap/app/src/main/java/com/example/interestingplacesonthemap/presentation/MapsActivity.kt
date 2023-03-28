package com.example.interestingplacesonthemap.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.example.interestingplacesonthemap.App
import com.example.interestingplacesonthemap.R
import com.example.interestingplacesonthemap.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapsBinding

    // вместо by viewModels() сделаем по даггеровски. Сама инициализация в начале onCreate.
    @javax.inject.Inject
    lateinit var vmFactory: MapsViewModelFactory
    private lateinit var viewModel: MapsViewModel

    private var map: GoogleMap? = null
    private lateinit var fusedClient: FusedLocationProviderClient
    private var locationListener: LocationSource.OnLocationChangedListener? = null

    private var needAnimateCamera = false
    private var needMoveCamera = true

    //хандлер ниже в коде будет ставить флаг needMoveCamera = false и выполнять задержку
    private val handler = Handler(Looper.getMainLooper())

    //после того как хандлер отработал задержку возвращаем флаг true
    private val cameraMovedRunnable = Runnable { needMoveCamera = true }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.values.isNotEmpty() && map.values.all { it }) startLocation()
        }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                //передача координат в листнер
                locationListener?.onLocationChanged(location)
                //задаем скорость в текствью он написал такое "%.1f m/s" и отправил это в ресурс string\speed
                binding.speed.text = getString(R.string.speed, location.speed)
                //анимация камеры (теперь она будет смотреть на наш маркер)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    15f
                )
                //отсылаем во вьюмодель наши координаты
                viewModel.myLocation.value = location
                //обновляем фичи на карте
                updateMap(location)

                if (needMoveCamera) { //чтобы при создании вью камера была без анимации, а потом уже с ней
                    if (needAnimateCamera) map?.animateCamera(cameraUpdate)
                    else {
                        needAnimateCamera = true
                        map?.moveCamera(cameraUpdate)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Подключаем вьюмодель
        (applicationContext as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this, vmFactory)[MapsViewModel::class.java]

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        //создадим звуковое уведомление, срабатывающие при запросе маршрута к репо
        val soundOnRequest = MediaPlayer.create(this, R.raw.sound_on_request_with_my_voice)

        //установим в нумберпикер список прокрутки и листнер
        //эти значения пользователь задаёт как радиус в метрах, где отображаются маркеры
        customizeNumberPicker()

        //слушатель на касание экрана с картой (сказал что готовый слушатель не нашел, поэтому написал свой)
        //здесь хандлер ставит флаг needMoveCamera = false и выполняет задержку
        binding.mapOverlay.setOnTouchListener { _, _ ->
            handler.removeCallbacks(cameraMovedRunnable)
            needMoveCamera = false
            handler.postDelayed(cameraMovedRunnable, 30000)
            false
        }

        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        mapFragment.getMapAsync { googleMap ->
            map = googleMap
            checkPermissions()
            with(googleMap.uiSettings) {
                this.isZoomControlsEnabled = true
                isMyLocationButtonEnabled = true
            }
            //задаем ресурс, от куда карта будет получать координаты текущего местоположения
            googleMap.setLocationSource(object : LocationSource {
                override fun activate(p0: LocationSource.OnLocationChangedListener) {
                    locationListener = p0
                }

                override fun deactivate() {
                    locationListener = null
                }
            })
            //ВНИМАНИЕ!!! подписка на данные вьюмодели и листнеры написаны внутри .getMapAsync{}
            //т.к. после поворота экрана, какое-то время map == null,
            //поэтому ждем пока .getMapAsync установит карту в map,
            //устанавливаем маркеры и рисуем путь до точки:
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.flowPlacesList.collect {
                        viewModel.myLocation.value?.let { it1 -> updateMap(it1) }
                    }
                }
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.flowPathToPoint.collect {
                        viewModel.myLocation.value?.let { it1 -> updateMap(it1) }
                    }
                }
            }
            //кликнутый маркер меняет цвет, появляется инфо, идет запрос к API поиска пути
            map?.setOnMarkerClickListener { marker ->
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                marker.showInfoWindow()
                viewModel.checkedMarker.value = marker
                true
            }
            //короткий клик на пустое место карты, чтобы отменить выбор маркера
            map?.setOnMapClickListener {
                viewModel.checkedMarker.value = null
                binding.distance.text = ""
            }
            //долгое нажатие создаёт "myMarker" в этой точке
            map?.setOnMapLongClickListener {
                val options = MarkerOptions()
                    .position(it)
                    .title("myMarker")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                map?.addMarker(options)?.showInfoWindow()
                viewModel.checkedMarker.value = map?.addMarker(options)
            }
        }
        //звуковой сигнал, что был запрос к репо
        lifecycleScope.launch {
            viewModel.notifyAboutRequestChannel.collect {
                soundOnRequest.start()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    override fun onStop() {
        super.onStop()
        fusedClient.removeLocationUpdates(locationCallback)
        needAnimateCamera = false
    }

    private fun customizeNumberPicker() {
        //Список прокрутки 100..10000 с прогрессирующим шагом 100->500->1000,
        //список виджет хранит как Array<String>, а обращение к элементу списка делается через индекс
        val valuesList =
            ((0..900 step 100) + (1000..4500 step 500) + (5000..10000 step 1000))
                .map { it.toString() }.toTypedArray()
        binding.numberPicker.maxValue = valuesList.lastIndex
        binding.numberPicker.minValue = 0
        binding.numberPicker.displayedValues = valuesList
        binding.numberPicker.value = valuesList.indexOfFirst { it == viewModel.radiusMarkersScope.value.toString() }
        binding.numberPicker.background.alpha = 100
        binding.numberPicker.setOnValueChangedListener { _, _, newVal ->
            viewModel.radiusMarkersScope.value = valuesList[newVal].toInt()
        }
    }

    private fun updateMap(myLocation: Location) {
        //чтобы удалить маркеры за пределами радиуса, сначала полностью очищаем карту
        //заодно удаляются не актуальные пути до точки
        map?.clear()
        //интересные места устанавливаем в качестве маркеров
        viewModel.flowPlacesList.value?.forEach {
            map!!.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            it.geometry.coordinates.last(),
                            it.geometry.coordinates.first()
                        )
                    )
                    .title(it.properties.name.ifEmpty { "no name" })
            )
        }
        //выбранный пользователем маркер ставится заново
        if (viewModel.checkedMarker.value != null) {
            map!!.addMarker(
                MarkerOptions()
                    .position(viewModel.checkedMarker.value!!.position)
                    .title(viewModel.checkedMarker.value!!.title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )?.showInfoWindow()//постоянно держим инфоокно над выбранным маркером

            //выводим дистанцию по азимуту до выбранного маркера
            val targetLocation = Location("")
            targetLocation.latitude = viewModel.checkedMarker.value!!.position.latitude
            targetLocation.longitude = viewModel.checkedMarker.value!!.position.longitude
            binding.azimuthDistance.text = getString(
                R.string.azimuth_distance,
                myLocation.distanceTo(targetLocation)
            )
            //рисуем азимут до маркера
            val azimuthOptions = PolylineOptions().width(3f).color(Color.GREEN).geodesic(true)
            azimuthOptions.add(LatLng(myLocation.latitude, myLocation.longitude))
            azimuthOptions.add(LatLng(targetLocation.latitude, targetLocation.longitude))
            map!!.addPolyline(azimuthOptions)

            //выводим длину пути и рисуем трек до маркера, если он выбран
            if (viewModel.flowPathToPoint.value != null) {
                val routeToPoint = viewModel.flowPathToPoint.value!!.routes.first().legs.first()

                binding.distance.text =
                    getString(R.string.distance, routeToPoint.summary.lengthInMeters)

                val routeOptions = PolylineOptions().width(9f).color(Color.BLUE).geodesic(true)
                routeOptions.add(LatLng(myLocation.latitude, myLocation.longitude))

                routeToPoint.points.forEach { routeOptions.add(LatLng(it.latitude, it.longitude)) }
                map!!.addPolyline(routeOptions)

                //очерчиваем speedDependentDistance - (значение, используемое во вьюмодели,
                //при определении, когда пора обновить данные в репозитории)
                val circleOptions = CircleOptions()
                    .center(LatLng(myLocation.latitude, myLocation.longitude))
                    .radius(viewModel.speedDependentDistance().toDouble())
                    .strokeColor(Color.BLACK)
                    .strokeWidth(3f)
                map!!.addCircle(circleOptions)
            }
        }
        binding.countInfo.text = getString(R.string.count_info,"loc=${viewModel.locationCount} rout=${viewModel.routeRequestCount} mark=${viewModel.markerRequestCount}")
        binding.exceptionInfo.text = viewModel.exceptionList.joinToString("\n")
    }

    @SuppressLint("MissingPermission")
    private fun startLocation() {
        map?.isMyLocationEnabled = true
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1_000).build()

        fusedClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun checkPermissions() {
        if (REQUEST_PERMISSIONS.all { permission ->
                ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }) startLocation()
        else launcher.launch(REQUEST_PERMISSIONS)
    }

    companion object {
        private val REQUEST_PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}