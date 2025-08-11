package tellme.sairajpatil108.tellme360

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tellme.sairajpatil108.tellme360.navigation.AppNavigation
import tellme.sairajpatil108.tellme360.navigation.Screen
import tellme.sairajpatil108.tellme360.presentation.screens.*
import tellme.sairajpatil108.tellme360.ui.theme.TellMe360Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    TellMe360Theme {
        TellMe360App()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TellMe360App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var seriesId by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                AppNavigation.bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Text(screen.title.first().toString()) },
                        label = { Text(screen.title) },
                        selected = currentScreen == screen,
                        onClick = {
                            currentScreen = screen
                            seriesId = null
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Home -> HomeScreen(
                    onNavigateToSeries = { id ->
                        seriesId = id
                        currentScreen = Screen.Series
                    }
                )
                Screen.VRVideos -> VRVideosScreen()
                Screen.Series -> {
                    if (seriesId != null) {
                        SeriesDetailScreen(
                            seriesId = seriesId!!,
                            onBackPressed = {
                                seriesId = null
                                currentScreen = Screen.Series
                            }
                        )
                    } else {
                        SeriesScreen(
                            onNavigateToSeriesDetail = { id ->
                                seriesId = id
                            }
                        )
                    }
                }
                Screen.Account -> AccountScreen()
            }
        }
    }
}