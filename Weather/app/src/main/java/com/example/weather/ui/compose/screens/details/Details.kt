package com.example.weather.ui.compose.screens.details

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather.ui.viewmodels.DetailsViewModel

@Composable
fun Details(
    cityId: String,
    onBackClick: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel(),
) {
    viewModel.getWeather(cityId)

    val state by viewModel.cityWithWeather.collectAsStateWithLifecycle()

    var dialogOpen by remember { mutableStateOf(false) }
    if (dialogOpen) {
        DelCityDialog(
            dialogClose = { dialogOpen = false },
            cityId = cityId,
            onDelCity = viewModel::delCity,
            navigateToPopBack = onBackClick
        )
    }

    Column {

        Row(modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray)
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = 8.dp, end = 20.dp)
                    .clickable { onBackClick() },
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null
            )

            state?.city?.let { Text(modifier = Modifier.fillMaxWidth(0.8f), text = it.name) }

            Icon(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
                    .padding(end = 8.dp)
                    .clickable { dialogOpen = true },
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        }

        LazyColumn(
            modifier = Modifier
                .padding(start = 14.dp)
                .fillMaxHeight(
                    if (LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE) 0.84f
                    else 0.94f
                ),
            state = rememberLazyListState()
        ) {
            state?.weather?.let { listWeather ->
                items(count = listWeather.size) {index ->
                    listWeather[index].let {
                        Text(text = "${it.data}  ${it.time} - ${it.temperature}")
                    }
                }
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray)
            .wrapContentWidth(Alignment.CenterHorizontally)
        )  {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 50.dp, vertical = 10.dp)
                    .clickable {
                        if (state != null) {
                            viewModel.refresh(
                                cityId = state!!.city.cityId,
                                lat = state!!.city.lat,
                                lon = state!!.city.lon
                            )
                        }
                    },
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color.Green
            )

            if (!state?.weather.isNullOrEmpty()) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 50.dp, vertical = 10.dp)
                        .clickable { viewModel.deleteWeather() },
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }
    }
}