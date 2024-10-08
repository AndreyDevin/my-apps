package com.example.weather.ui.compose.screens.details

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DelCityDialog(
    dialogClose: () -> Unit,
    cityId: String,
    onDelCity: (String) -> Unit,
    navigateToPopBack: () -> Unit
) {
    AlertDialog(
        onDismissRequest = dialogClose,//когда вне поля
        dismissButton = {
            TextButton(onClick = dialogClose) { Text(text = "BACK") }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDelCity(cityId)
                    navigateToPopBack()
                },
                content = { Text(text = "DELETE THIS CITY") }
            )
        }
    )
}