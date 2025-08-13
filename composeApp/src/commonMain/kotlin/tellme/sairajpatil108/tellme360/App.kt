package tellme.sairajpatil108.tellme360

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import tellme.sairajpatil108.tellme360.navigation.AppNavigation
import tellme.sairajpatil108.tellme360.navigation.Navigator
import tellme.sairajpatil108.tellme360.navigation.Screen
import tellme.sairajpatil108.tellme360.navigation.rememberNavigator
import tellme.sairajpatil108.tellme360.navigation.AppNavHost
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
    val navigator = rememberNavigator()
    val layoutDirection = LocalLayoutDirection.current
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar {
                AppNavigation.bottomNavItems.forEach { screen ->
                    val selected = navigator.currentBottomRoot() == screen
                    NavigationBarItem(
                        icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                        label = { Text(screen.title) },
                        selected = selected,
                        onClick = { navigator.navigateRoot(screen) }
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
            AppNavHost(navigator)
        }
    }
}