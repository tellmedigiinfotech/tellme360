package tellme.sairajpatil108.tellme360.navigation

import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Home : Screen("home", "Home", null)
    object VRVideos : Screen("vr_videos", "VR Videos", null)
    object Series : Screen("series", "Series", null)
    object Account : Screen("account", "Account", null)
    
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
    const val ACCOUNT_ROUTE = "account"
    
    val bottomNavItems = listOf(
        Screen.Home,
        Screen.VRVideos,
        Screen.Series,
        Screen.Account
    )
}
