package tellme.sairajpatil108.tellme360.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class Navigator(initial: Screen = Screen.Home) {
    private val backStack = mutableStateListOf<Screen>()
    var current: Screen by mutableStateOf(initial)
        private set

    private fun isRoot(screen: Screen): Boolean =
        screen === Screen.Home || screen === Screen.VRVideos || screen === Screen.Series || screen === Screen.Account

    fun navigate(target: Screen) {
        if (current == target) return
        backStack.add(current)
        current = target
    }

    fun back() {
        if (backStack.isNotEmpty()) {
            current = backStack.removeLast()
        }
    }

    // For bottom bar selection: map detail screens to their root section
    fun currentBottomRoot(): Screen = when (val s = current) {
        is Screen.SeriesDetail -> Screen.Series
        else -> s
    }

    fun navigateRoot(targetRoot: Screen) {
        if (!isRoot(targetRoot)) {
            navigate(targetRoot)
            return
        }
        backStack.clear()
        current = targetRoot
    }
}

@Composable
fun rememberNavigator(initial: Screen = Screen.Home): Navigator = remember { Navigator(initial) }


