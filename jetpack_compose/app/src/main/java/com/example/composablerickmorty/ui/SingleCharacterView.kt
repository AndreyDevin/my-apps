package com.example.composablerickmorty.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.composablerickmorty.dto.Character

@Composable
fun SingleCharacterView(
    character: Character,
    backPressed: () -> Unit
) {
    BackHandler { backPressed() }

    if ( LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) { Character(character = character) }
    } else {
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxHeight()
        ) { Character(character = character) }
    }
}

@Composable
fun Character(character: Character) {
    GlideImageWithPreview(
        data = character.image,
        modifier = Modifier.size(320.dp)
    )
    Column (modifier = Modifier.padding(10.dp)){
        Text(text = character.name)
        Text(text = character.species)
        Text(text = character.status)
    }
}