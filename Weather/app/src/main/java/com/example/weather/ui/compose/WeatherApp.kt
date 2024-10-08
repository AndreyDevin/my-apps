package com.example.weather.ui.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.weather.ui.compose.navigation.NavBottomBar
import com.example.weather.ui.compose.navigation.WeatherNavHost

@Composable
fun WeatherApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { NavBottomBar(navController = navController) }
    ) { innerPadding ->
        WeatherNavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}