package com.example.weather.ui.compose.screens.search

import android.location.Address
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AddressItem(address: Address, onClick: (Address) -> Unit) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Cyan)
            .clickable { onClick(address) },
        text = "${address.featureName}, ${address.subAdminArea}, ${address.adminArea}"
    )
}