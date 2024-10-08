package com.example.weather.ui.compose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.weather.ui.Screens

@Composable
fun NavBottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            label = {
                Text("ПОИСК")
            },
            selected = navBackStackEntry?.destination?.hasRoute<Screens.Search>() ?: false,
            onClick = {
                navController.navigate(Screens.Search) {
                    popUpTo<Screens.Search> { inclusive = false }
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null
                )
            },
            label = {
                Text("ГОРОДА")
            },
            selected = navBackStackEntry?.destination?.hasRoute<Screens.SavedCities>() ?: false,
            onClick = {
                navController.navigate(Screens.SavedCities) {
                    popUpTo<Screens.SavedCities> { inclusive = false }
                    launchSingleTop = true
                }
            }
        )
    }
}
