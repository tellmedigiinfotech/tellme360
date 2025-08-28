package tellme.sairajpatil108.tellme360.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tellme.sairajpatil108.tellme360.presentation.screens.*
import tellme.sairajpatil108.tellme360.data.model.VideoContent

@Composable
fun AppNavHost(navigator: Navigator) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (val screen = navigator.current) {
            Screen.Home -> HomeScreen(onNavigateToSeries = { id ->
                navigator.navigate(Screen.SeriesDetail(id))
            })
            Screen.VRVideos -> VRVideosScreen(onNavigateToVideoDetail = { id ->
                navigator.navigate(Screen.VideoDetail(id))
            })
            Screen.Series -> SeriesScreen(onNavigateToSeriesDetail = { id ->
                navigator.navigate(Screen.SeriesDetail(id))
            })
            is Screen.SeriesDetail -> SeriesDetailScreen(
                seriesId = screen.seriesId,
                onBackPressed = { navigator.back() }
            )
            is Screen.VideoDetail -> {
                val vrVideos = listOf(
                    VideoContent(
                        id = "1",
                        title = "Sample VR Test Video",
                        description = "This is a sample 360-degree VR video for testing purposes. Experience immersive virtual reality content.",
                        thumbnailUrl = "",
                        videoUrl = "https://tellme360.media/videos/2025-07-30/14-29-48-615460/mp4_files/1080p.mp4",
                        duration = "10:00",
                        category = "Test",
                        isVR = true,
                        rating = 5.0f,
                        views = 1000
                    )
                )
                val video = vrVideos.find { it.id == screen.videoId } ?: vrVideos.first()
                VideoDetailScreen(
                    video = video,
                    onBackClick = { navigator.back() }
                )
            }
            Screen.Account -> AccountScreen()
        }
    }
}


