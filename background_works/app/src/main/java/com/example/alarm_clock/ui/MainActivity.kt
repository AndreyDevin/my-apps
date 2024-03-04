package com.example.alarm_clock.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.alarm_clock.ui.theme.Alarm_ClockTheme
import com.example.alarm_clock.util.calculate_sunrise.suncalc.SunTimes
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {

    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.values.all { it }) return@registerForActivityResult
            else Toast.makeText(this, "permissions is not Granted", Toast.LENGTH_LONG).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissions()

        //на память пример, как в коин передавать аргументы
        //val viewModel by viewModel<MainViewModel> { parametersOf(this::createAlarmIntent) }
        val viewModel by viewModel<MainViewModel>()

        setContent {
            Alarm_ClockTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScreen(
                        sunriseTimeFlow = viewModel.sunriseTimeFlow,
                        timePickerListener = viewModel::createAlarmTime
                    )
                }
            }
        }
    }

    private fun checkPermissions() {
        val isAllGranted = REQUEST_PERMISSIONS.all { permissions ->
            ContextCompat.checkSelfPermission(this, permissions) == PackageManager.PERMISSION_GRANTED
        }
        if (isAllGranted) {
            Toast.makeText(this, "permissions is granted", Toast.LENGTH_LONG).show()
            return
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
    }

    companion object {
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT >= 31) {
                add(Manifest.permission.SCHEDULE_EXACT_ALARM)
                if (Build.VERSION.SDK_INT >= 33) add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()
    }
}

@Composable
fun AppScreen(
    sunriseTimeFlow: StateFlow<SunTimes?>,
    timePickerListener: (
        Int,
        (Result<ZonedDateTime>) -> Unit
    ) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MessageToUser(sunriseTimeFlow)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "You can set alarm clock\n" +
                    "in relation to sunrise:\n" +
                    "before (negative) or after",
            fontSize = 12.sp
        )
        Text(text = "\u21C4", fontSize = 32.sp)
        //TimePicker(context, timePickerListener)
        NumberPicker(listener = timePickerListener)
    }
}

@Composable
fun MessageToUser(sunriseTimeFlow: StateFlow<SunTimes?>) {
    val state = sunriseTimeFlow.collectAsState(initial = null)
    val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm:ss")
    Text(
        text = state.value.let {
            StringBuilder()
                .append("Time zone: ${it?.rise?.zone}")
                .append("\nSunrise: ${it?.rise?.format(formatter)}")
                .append("\nSunset: ${it?.set?.format(formatter)}")
                .toString()
        }
    )
}

@Composable
fun NumberPicker(listener: (Int, (Result<ZonedDateTime>) -> Unit) -> Unit) {
//тут такая загогулина: листнер ссылается на fun createAlarmTime во вьюмодели,
//функция принимает минуты от нумберпикера, чтобы их прибавить ко времени восхода и создать аларм интент,
//а также принимает коллбэк назад сюда.
//В коллбэк передаётся время аларма, чтобы просто написать его на экране для удобства.
//Не знаю допустима ли такая кракозябра, но я хотел просто поюзать коллбэки, убедится что так тоже работает :)

    val incrementElementList: List<Int> =
                (-300..-240 step 60) +
                (-180..-60 step 10) +
                (-55..-15 step 5) +
                (-14..-1 step 1) +
                (0..14 step 1) +
                (15..55 step 5) +
                (60..180 step 10) +
                (240..300 step 60)

    val pickerItems: List<Pair<Int, String>> =
        incrementElementList.map { it to convertIntOfMinutesToString(it/60, it%60) }

    val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy, hh:mm:ss a")
    var textInfo by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            state = LazyListState(pickerItems.size / 2 - 4)
        ) {
            items(
                count = pickerItems.size,
            ) {
                pickerItems[it].also { item ->
                    ClickableText(
                        modifier = Modifier.width(45.dp),
                        text = AnnotatedString(item.second),
                        onClick = {
                            textInfo = infoOfAlarmTimeText(item.first)

                            listener(item.first) { callbackOfAlarmTime ->
                                if (callbackOfAlarmTime.isSuccess) textInfo +=
                                    "\n${callbackOfAlarmTime.getOrNull()?.format(formatter)}"
                            }
                        }
                    )
                }
            }
        }
        Text(text = textInfo)
    }
}

fun infoOfAlarmTimeText(clickedNumber: Int): String {
    return when {
        clickedNumber < 0 -> "Alarm at $clickedNumber minutes before sunrise:"
        clickedNumber == 0 -> "The alarm at sunrise:"
        else -> "Alarm at $clickedNumber minutes after sunrise:"
    }
}

fun convertIntOfMinutesToString(h: Int, m: Int): String {
    if (h == 0 && m == 0) return "  0"
    val pref = if (h < 0 || m < 0) "-" else ""
    val hour = if (h == 0) "" else h.absoluteValue.toString()
    val min = if (m == 0) "" else m.absoluteValue.toString() + if (h == 0) 'm' else ""
    val delimiter = if (h != 0 && m != 0) ":" else if (m == 0) "h" else ""
    return pref + hour + delimiter + min
}
//Сначала вместо кастомного нумберпикера был библиотечный TimePicker,
//поменял потому, что он не даёт отрицательные значения, нельзя проснуться до рассвета
//оставлю на память
/*
@Composable
fun TimePicker(
    context: Context,
    listener: (
        Int,
        Int,
        (Result<ZonedDateTime>) -> Unit
    ) -> Unit
) {
    val calendar = Calendar.getInstance()
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]
    val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy, hh:mm:ss a")
    var textInfo by rememberSaveable { mutableStateOf("") }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, h: Int, m: Int ->
            textInfo = StringBuilder()
                .append("Будильник сработает через\n")
                .append(if (h > 0) "$h ч " else "")
                .append("$m мин. после восхода солнца:")
                .toString()
            listener(h, m) {
                if (it.isSuccess) textInfo += "\n${it.getOrNull()?.format(formatter)}"
            }
        },
        hour,
        minute,
        true
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier.padding(vertical = 16.dp),
            onClick = { timePickerDialog.show() }
        ) { Text(text = "Select alarm time after sunrise") }

        Text(text = textInfo)
    }
}*/
