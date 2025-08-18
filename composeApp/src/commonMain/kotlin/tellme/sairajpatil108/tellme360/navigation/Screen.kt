package tellme.sairajpatil108.tellme360.navigation

import compose.icons.FeatherIcons
import compose.icons.feathericons.Home
import compose.icons.feathericons.Bookmark
import compose.icons.feathericons.User
import compose.icons.feathericons.Camera
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Home : Screen("home", "Home", FeatherIcons.Home)
    object VRVideos : Screen("vr_videos", "VR Videos", FeatherIcons.Camera)
    object Series : Screen("series", "Series", FeatherIcons.Bookmark)
    object Account : Screen("account", "Account", FeatherIcons.User)
    data class SeriesDetail(val seriesId: String) : Screen("series_detail/$seriesId", "Series Detail", null)
    data class VideoDetail(val videoId: String) : Screen("video_detail/$videoId", "Video Detail", null)
    
    companion object {
        fun fromRoute(route: String?): Screen {
            return when (route?.substringBefore("/")) {
                Home.route -> Home
                VRVideos.route -> VRVideos
                Series.route -> Series
                Account.route -> Account
                null -> Home
                else -> Home
            }
        }
    }
}

object AppNavigation {
    const val HOME_ROUTE = "home"
    const val VR_VIDEOS_ROUTE = "vr_videos"
    const val SERIES_ROUTE = "series"
    const val SERIES_DETAIL_ROUTE = "series_detail/{seriesId}"
    const val VIDEO_DETAIL_ROUTE = "video_detail/{videoId}"
    const val ACCOUNT_ROUTE = "account"
    
    val bottomNavItems = listOf(
        Screen.Home,
        Screen.VRVideos,
        Screen.Series,
        Screen.Account
    )
}
