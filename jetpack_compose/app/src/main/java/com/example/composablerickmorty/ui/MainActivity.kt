package com.example.composablerickmorty.ui

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.composablerickmorty.Injection
import com.example.composablerickmorty.ui.theme.ComposableRickMortyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(owner = this)
        )[MainViewModel::class.java]

        lifecycleScope.launch {
            viewModel.state.collect { uiState ->
                setContent {
                    ComposableRickMortyTheme {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            bottomBar = {
                                if ( LocalConfiguration.current.orientation == ORIENTATION_PORTRAIT ) {
                                    NavBottomBar(
                                        charactersOnClick = { viewModel.shiftUiStateToCharactersList() },
                                        locationsOnClick = { viewModel.shiftUiStateToLocationsList() },
                                        uiState = uiState
                                    )
                                }
                            }
                        ) { padding ->
                            Row(
                                modifier = Modifier.padding(padding)
                            ) {
                                if ( LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE ) {
                                    LandscapeNavBar(
                                        charactersOnClick = { viewModel.shiftUiStateToCharactersList() },
                                        locationsOnClick = { viewModel.shiftUiStateToLocationsList() },
                                        uiState = uiState
                                    )
                                }
                                when (uiState) {
                                    is UiData.InitState -> {}
                                    is UiData.SingleCharacter -> SingleCharacterView(
                                        character = uiState.singleCharacter,
                                        backPressed = { viewModel.onBackPressed() }
                                    )
                                    is UiData.CharacterPagingData -> CharactersList(
                                        pagingDataFlow = uiState.characterPagingData,
                                        onItemClick = viewModel::shiftUiStateToCharacter,
                                        backPressed = { viewModel.onBackPressed() }
                                    )
                                    is UiData.LocationPagingData -> LocationsList(
                                        pagingDataFlow = uiState.locationPagingData,
                                        backPressed = { viewModel.onBackPressed() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NavBottomBar(
    modifier: Modifier = Modifier,
    charactersOnClick: () -> Unit,
    locationsOnClick: () -> Unit,
    uiState: UiData
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = null
                )
            },
            label = {
                Text("Characters")
            },
            selected = uiState is UiData.CharacterPagingData,
            onClick = { charactersOnClick() }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null
                )
            },
            label = {
                Text("Locations")
            },
            selected = uiState is UiData.LocationPagingData,
            onClick = { locationsOnClick() }
        )
    }
}

@Composable
private fun LandscapeNavBar(
    modifier: Modifier = Modifier,
    charactersOnClick: () -> Unit,
    locationsOnClick: () -> Unit,
    uiState: UiData
    ) {
    NavigationRail(
        modifier = modifier.padding(end = 8.dp),
        containerColor = MaterialTheme.colorScheme.surfaceVariant//background,
    ) {
        Column(
            modifier = modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = null
                    )
                },
                label = {
                    Text("Characters")
                },
                selected = uiState is UiData.CharacterPagingData,
                onClick = { charactersOnClick() }
            )
            Spacer(modifier = Modifier.height(60.dp))
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null
                    )
                },
                label = {
                    Text("Locations")
                },
                selected = uiState is UiData.LocationPagingData,
                onClick = { locationsOnClick() }
            )
        }
    }
}