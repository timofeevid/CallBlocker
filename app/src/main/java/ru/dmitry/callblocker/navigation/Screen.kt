package ru.dmitry.callblocker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import ru.dmitry.callblocker.R

sealed class Screen(
    val route: String,
    val resourceId: Int,
    val icon: ImageVector
) {
    data object Home : Screen("home", R.string.home, Icons.Default.Home)
    data object Settings : Screen("settings", R.string.settings, Icons.Default.Settings)
}