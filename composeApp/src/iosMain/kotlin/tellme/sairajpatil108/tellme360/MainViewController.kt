package tellme.sairajpatil108.tellme360

import androidx.compose.ui.window.ComposeUIViewController
import tellme.sairajpatil108.tellme360.ios.RootControllerHolder

fun MainViewController() = ComposeUIViewController { App() }.also { controller ->
    RootControllerHolder.rootController = controller
}