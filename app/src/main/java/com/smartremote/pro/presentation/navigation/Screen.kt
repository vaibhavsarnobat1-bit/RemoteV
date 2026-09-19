package com.smartremote.pro.presentation.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Devices")
    object Remote : Screen("remote/{deviceId}", "Remote") {
        fun createRoute(deviceId: String) = "remote/$deviceId"
    }
    object AirtelXstream : Screen("airtel", "Airtel Xstream")
    object Macros : Screen("macros", "Automation")
    object Settings : Screen("settings", "Settings")
    object Onboarding : Screen("onboarding", "Welcome")
}
