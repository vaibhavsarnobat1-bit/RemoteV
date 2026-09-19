package com.smartremote.pro.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartremote.pro.core.ir.IRLearningManager
import com.smartremote.pro.presentation.navigation.Screen
import com.smartremote.pro.presentation.theme.AirtelRed
import com.smartremote.pro.presentation.theme.CardDark
import com.smartremote.pro.presentation.theme.CyberCyan
import com.smartremote.pro.presentation.theme.DarkBackground
import com.smartremote.pro.presentation.theme.TextMuted
import com.smartremote.pro.presentation.theme.TextPrimary
import com.smartremote.pro.presentation.ui.airtel.AirtelXstreamScreen
import com.smartremote.pro.presentation.ui.home.HomeScreen
import com.smartremote.pro.presentation.ui.macro.MacroScreen
import com.smartremote.pro.presentation.ui.profiles.ProfileManagementDialog
import com.smartremote.pro.presentation.ui.remote.RemoteScreen
import com.smartremote.pro.presentation.ui.settings.SettingsScreen
import com.smartremote.pro.presentation.ui.voice.VoiceControlSheet
import com.smartremote.pro.presentation.viewmodels.AirtelXstreamViewModel
import com.smartremote.pro.presentation.viewmodels.HomeViewModel
import com.smartremote.pro.presentation.viewmodels.MacroViewModel
import com.smartremote.pro.presentation.viewmodels.ProfileViewModel
import com.smartremote.pro.presentation.viewmodels.RemoteViewModel
import com.smartremote.pro.presentation.viewmodels.SettingsViewModel
import com.smartremote.pro.presentation.viewmodels.VoiceViewModel

@Composable
fun MainAppScaffold(
    homeViewModel: HomeViewModel,
    remoteViewModel: RemoteViewModel,
    airtelViewModel: AirtelXstreamViewModel,
    macroViewModel: MacroViewModel,
    voiceViewModel: VoiceViewModel,
    profileViewModel: ProfileViewModel,
    settingsViewModel: SettingsViewModel,
    learningManager: IRLearningManager
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showVoiceSheet by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = CardDark,
                contentColor = TextPrimary
            ) {
                // Home
                NavigationBarItem(
                    selected = currentRoute == Screen.Home.route,
                    onClick = { navController.navigate(Screen.Home.route) },
                    icon = { Icon(Icons.Default.Tv, contentDescription = "Devices") },
                    label = { Text("Devices", fontSize = 11.sp) },
                    colors = navItemColors()
                )

                // Airtel Xstream DTH
                NavigationBarItem(
                    selected = currentRoute == Screen.AirtelXstream.route,
                    onClick = { navController.navigate(Screen.AirtelXstream.route) },
                    icon = { Icon(Icons.Default.SettingsRemote, contentDescription = "Airtel") },
                    label = { Text("Airtel", fontSize = 11.sp) },
                    colors = navItemColors(selectedColor = AirtelRed)
                )

                // Voice FAB Center Slot Placeholder
                NavigationBarItem(
                    selected = false,
                    onClick = { showVoiceSheet = true },
                    icon = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .offset(y = (-14).dp)
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(CyberCyan)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Control",
                                tint = DarkBackground,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    label = { Text("") },
                    colors = navItemColors()
                )

                // Macros Automation
                NavigationBarItem(
                    selected = currentRoute == Screen.Macros.route,
                    onClick = { navController.navigate(Screen.Macros.route) },
                    icon = { Icon(Icons.Default.Bolt, contentDescription = "Macros") },
                    label = { Text("Macros", fontSize = 11.sp) },
                    colors = navItemColors()
                )

                // Settings
                NavigationBarItem(
                    selected = currentRoute == Screen.Settings.route,
                    onClick = { navController.navigate(Screen.Settings.route) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", fontSize = 11.sp) },
                    colors = navItemColors()
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onDeviceClick = { device ->
                        navController.navigate(Screen.Remote.createRoute(device.id))
                    },
                    onQuickMacroClick = {
                        navController.navigate(Screen.Macros.route)
                    }
                )
            }

            composable(Screen.Remote.route) { backStackEntry ->
                val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""
                RemoteScreen(
                    deviceId = deviceId,
                    viewModel = remoteViewModel,
                    learningManager = learningManager,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AirtelXstream.route) {
                AirtelXstreamScreen(
                    viewModel = airtelViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Macros.route) {
                MacroScreen(viewModel = macroViewModel)
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onOpenProfiles = { showProfileDialog = true }
                )
            }
        }

        if (showVoiceSheet) {
            VoiceControlSheet(
                viewModel = voiceViewModel,
                onDismiss = { showVoiceSheet = false }
            )
        }

        if (showProfileDialog) {
            ProfileManagementDialog(
                viewModel = profileViewModel,
                onDismiss = { showProfileDialog = false }
            )
        }
    }
}

@Composable
private fun navItemColors(selectedColor: androidx.compose.ui.graphics.Color = CyberCyan) = NavigationBarItemDefaults.colors(
    selectedIconColor = selectedColor,
    selectedTextColor = selectedColor,
    unselectedIconColor = TextMuted,
    unselectedTextColor = TextMuted,
    indicatorColor = CardDark
)
