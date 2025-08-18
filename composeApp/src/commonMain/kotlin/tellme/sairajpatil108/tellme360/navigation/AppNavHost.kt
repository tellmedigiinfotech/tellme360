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
                        title = "VR Gaming Experience",
                        description = "Immerse yourself in the world of virtual reality gaming with this incredible 360-degree experience. Feel like you're actually inside the game world as you explore vast landscapes and engage in epic battles.",
                        thumbnailUrl = "",
                        videoUrl = "https://drive.google.com/uc?export=download&id=10oyKIe1AlliTD4HdS1j8fDxNo-zlVAbg",
                        duration = "25:30",
                        category = "Gaming",
                        isVR = true,
                        rating = 4.8f,
                        views = 35000
                    ),
                    VideoContent(
                        id = "2",
                        title = "Virtual Travel: Paris",
                        description = "Take a virtual journey through the beautiful streets of Paris. Experience the Eiffel Tower, Louvre Museum, and charming cafes as if you were really there.",
                        thumbnailUrl = "",
                        videoUrl = "https://drive.google.com/uc?export=download&id=10oyKIe1AlliTD4HdS1j8fDxNo-zlVAbg",
                        duration = "18:45",
                        category = "Travel",
                        isVR = true,
                        rating = 4.6f,
                        views = 22000
                    ),
                    VideoContent(
                        id = "3",
                        title = "VR Education: Solar System",
                        description = "Learn about planets, stars, and galaxies in this immersive educational experience. Perfect for students and space enthusiasts alike.",
                        thumbnailUrl = "",
                        videoUrl = "https://drive.google.com/uc?export=download&id=10oyKIe1AlliTD4HdS1j8fDxNo-zlVAbg",
                        duration = "32:15",
                        category = "Education",
                        isVR = true,
                        rating = 4.9f,
                        views = 28000
                    ),
                    VideoContent(
                        id = "4",
                        title = "VR Sports: Basketball",
                        description = "Experience basketball like never before in VR. Feel the energy of the court and the excitement of the game from unique perspectives.",
                        thumbnailUrl = "",
                        videoUrl = "https://drive.google.com/uc?export=download&id=10oyKIe1AlliTD4HdS1j8fDxNo-zlVAbg",
                        duration = "15:20",
                        category = "Sports",
                        isVR = true,
                        rating = 4.4f,
                        views = 18000
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


