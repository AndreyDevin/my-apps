package com.example.scorer

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scorer.ui.theme.ScorerTheme
import com.example.scorer.useCase.Timer

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val userDao = (application as App).db.dailyDataDao()
                val timer = Timer()
                return MainViewModel(userDao, timer) as T
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScorerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    val timerState = viewModel.timerState.collectAsStateWithLifecycle()
    val destination = viewModel.destination.collectAsStateWithLifecycle()
    val allWeeks = viewModel.allWeekList.collectAsStateWithLifecycle()
    val stateInitDialog = viewModel.stateInitDialog.collectAsStateWithLifecycle()
    val durationByMonth = viewModel.listOfDurationByMonth.collectAsStateWithLifecycle()

    if (stateInitDialog.value) InitDialog(
        dismiss = { viewModel.closeInitDialog(false) },
        conform = { viewModel.closeInitDialog(true) }
    )

    Column(
        Modifier.verticalScroll(rememberScrollState())
    ) {
        if (timerState.value) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                text = destination.value.toString(),
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            onClick = { viewModel.onTimer() }
        ) {
            Text(text = if (!timerState.value) "Start" else "Stop")
        }
        Row {
            if (durationByMonth.value != null) {
                    Column {
                        durationByMonth.value!!.asReversed().forEach { (month, durations) ->
                            Text(
                                text = "$month\n${minToTimeFormat(durations/60)}",
                                modifier = Modifier
                                    .size(40.dp, 60.dp)
                                    .padding(4.dp)
                                    .background(Color.Yellow),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            if (allWeeks.value != null) {
                Column {
                       allWeeks.value!!.forEach { Year( listWeeks = listOf(it), viewModel = viewModel ) }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Year(listWeeks: List<Map<Long, Long>>, viewModel: MainViewModel) {
    listWeeks.forEach { week ->
        Week(week = week.map { it.key to it.value }, viewModel = viewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Week(week: List<Pair<Long, Long>>, viewModel: MainViewModel) {
    Row(
        Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        week.asReversed().forEach {
            Day(
                key = it.first,
                value = it.second,
                viewModel = viewModel
            )
        }
        (7 - week.size).also {
            if (it > 0) repeat(it) {
                Day(
                    key = -1,
                    value = -1,
                    viewModel = viewModel
                )
            }
        }
        week.sumOf { it.second }.also {
            if (it > 0) {
                Text(
                    text = minToTimeFormat(it / 60),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .size(40.dp)
                        .padding(4.dp)
                )
            } else Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Day(
    key: Long,
    value: Long,
    viewModel: MainViewModel
) {

    var dialogOpen by remember { mutableStateOf(false) }
    if (dialogOpen) {
        ItemDialog(
            key = key,
            value = value,
            dialogClose = { dialogOpen = false},
            viewModel = viewModel
        )
    }
    Text(
        text = if (value>0) minToTimeFormat(value/60) else "",
        Modifier
            .wrapContentSize(Alignment.Center)
            .size(40.dp)
            .padding(4.dp)
            .background(
                when {
                    value > 0 -> Color.Green
                    value == 0L -> Color.LightGray
                    else -> {
                        Color.White
                    }
                }
            )
            .clickable { if (value >= 0) dialogOpen = !dialogOpen },
        fontSize = 12.sp
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ItemDialog(
    key: Long,
    value: Long,
    dialogClose: () -> Unit,
    viewModel: MainViewModel
) {
    AlertDialog(
        onDismissRequest = { dialogClose() },
        buttons = {
            Row {
                when {
                    value > 0 -> {
                        TextButton(onClick = {viewModel.updateDay(key, 600)}) {
                            Text(text = "+10m")
                        }
                        if (value >= 600) {
                            TextButton(onClick = {viewModel.updateDay(key, -600)}) {
                                Text(text = "-10m")
                            }
                        }
                        TextButton(onClick = {viewModel.deleteDay(key)}) {
                            Text(text = "delete")
                        }
                    }
                    value == 0L -> TextButton (onClick = {viewModel.insertToDataBase(key)}) {
                        Text(text = "create?")
                    }
                }
                TextButton(onClick = { dialogClose() }) {
                    Text(text = "back")
                }
            }
        }
    )
}

@Composable
fun InitDialog(
    dismiss: () -> Unit,
    conform: () -> Unit
) {
    AlertDialog(
        title = { Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
            text = "previous session is not closed"
        )},
        onDismissRequest = dismiss,
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
            ) {
                TextButton(onClick = conform) {
                    Text(text = "resume")
                }
                TextButton(onClick = dismiss) {
                    Text(text = "clear")
                }
            }
        }
    )
}

fun minToTimeFormat(min: Long): String {
    return "${if (min/60 > 0) " ${min/60} h" else "     -"}\n ${(if (min%60 < 1) "<1" else min%60)} m"
}
/*val localDateTimeList = db.value.map {
    LocalDateTime
        .ofInstant(
            Instant.ofEpochMilli(it.dateInMillis),
            ZoneId.systemDefault()
        ).plusHours(3)
}*/