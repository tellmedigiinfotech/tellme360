package tellme.sairajpatil108.tellme360.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tellme.sairajpatil108.tellme360.presentation.screens.*

@Composable
fun AppNavHost(navigator: Navigator) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (val screen = navigator.current) {
            Screen.Home -> HomeScreen(onNavigateToSeries = { id ->
                navigator.navigate(Screen.SeriesDetail(id))
            })
            Screen.VRVideos -> VRVideosScreen()
            Screen.Series -> SeriesScreen(onNavigateToSeriesDetail = { id ->
                navigator.navigate(Screen.SeriesDetail(id))
            })
            is Screen.SeriesDetail -> SeriesDetailScreen(
                seriesId = screen.seriesId,
                onBackPressed = { navigator.back() }
            )
            Screen.Account -> AccountScreen()
        }
    }
}


