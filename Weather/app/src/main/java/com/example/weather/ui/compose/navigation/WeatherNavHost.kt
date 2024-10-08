package com.example.weather.ui.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.weather.ui.Screens
import com.example.weather.ui.compose.screens.details.Details
import com.example.weather.ui.compose.screens.savedCities.SavedCities
import com.example.weather.ui.compose.screens.search.Search

@Composable
fun WeatherNavHost(
    modifier: Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screens.Search
    ) {
        composable<Screens.Search> {
            Search(
                onItemClick = { cityId ->
                    navController.navigate(Screens.Details(cityId = cityId)) {
                        popUpTo<Screens.Details> { inclusive = false }
                        launchSingleTop = true
                    // Как понимаю перед .navigate срабатывает popUpTo,
                    // который удаляет из стека всё что найдет перед <пункт_назначения>
                    // inclusive - англ. включительно
                    // launchSingleTop - это будет единственная запись с таким <пункт_назначения>
                    }
                }
            )
        }
        composable<Screens.SavedCities> {
            SavedCities(
                onCityClick = { cityId ->
                    navController.navigate(Screens.Details(cityId = cityId)) {
                        popUpTo<Screens.Details> { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<Screens.Details> {
            Details(
                cityId = it.toRoute<Screens.Details>().cityId,
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}