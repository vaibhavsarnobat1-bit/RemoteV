package com.smartremote.pro.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.smartremote.pro.SmartRemoteApp
import com.smartremote.pro.core.gesture.ShakeDetector
import com.smartremote.pro.core.nfc.NfcMacroHandler
import com.smartremote.pro.core.service.MiniRemoteNotificationService
import com.smartremote.pro.presentation.theme.SmartRemoteProTheme
import com.smartremote.pro.presentation.viewmodels.AirtelXstreamViewModel
import com.smartremote.pro.presentation.viewmodels.HomeViewModel
import com.smartremote.pro.presentation.viewmodels.MacroViewModel
import com.smartremote.pro.presentation.viewmodels.ProfileViewModel
import com.smartremote.pro.presentation.viewmodels.RemoteViewModel
import com.smartremote.pro.presentation.viewmodels.SettingsViewModel
import com.smartremote.pro.presentation.viewmodels.VoiceViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var shakeDetector: ShakeDetector

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Permissions handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as SmartRemoteApp
        val container = app.appContainer

        // Initialize ViewModels with AppContainer dependencies
        val homeViewModel = HomeViewModel(
            deviceRepository = container.deviceRepository,
            sendDeviceCommandUseCase = container.sendDeviceCommandUseCase,
            discoverNetworkDevicesUseCase = container.discoverNetworkDevicesUseCase,
            isPremiumUser = container.preferenceManager.isPremiumUser
        )

        val remoteViewModel = RemoteViewModel(
            deviceRepository = container.deviceRepository,
            sendDeviceCommandUseCase = container.sendDeviceCommandUseCase,
            smartTvClient = container.smartTvClient
        )

        val airtelViewModel = AirtelXstreamViewModel(
            channelRepository = container.channelRepository,
            deviceRepository = container.deviceRepository,
            sendDeviceCommandUseCase = container.sendDeviceCommandUseCase
        )

        val macroViewModel = MacroViewModel(
            macroRepository = container.macroRepository,
            executeMacroUseCase = container.executeMacroUseCase
        )

        val voiceViewModel = VoiceViewModel(
            voiceRecognizer = container.voiceRecognizer,
            parseVoiceCommandUseCase = container.parseVoiceCommandUseCase,
            sendDeviceCommandUseCase = container.sendDeviceCommandUseCase,
            executeMacroUseCase = container.executeMacroUseCase,
            deviceRepository = container.deviceRepository,
            settingsRepository = container.settingsRepository,
            textToSpeechManager = container.textToSpeechManager
        )

        val profileViewModel = ProfileViewModel(
            profileRepository = container.profileRepository
        )

        val settingsViewModel = SettingsViewModel(
            settingsRepository = container.settingsRepository,
            deviceRepository = container.deviceRepository,
            macroRepository = container.macroRepository,
            profileRepository = container.profileRepository,
            findMyRemoteBeep = container.findMyRemoteBeep
        )

        // Gesture: Shake phone to toggle pause/play
        shakeDetector = ShakeDetector(this) {
            lifecycleScope.launch {
                val devices = container.deviceRepository.getAllDevices().firstOrNull() ?: emptyList()
                val activeTv = devices.firstOrNull()
                if (activeTv != null) {
                    container.sendDeviceCommandUseCase(activeTv, "ok")
                }
            }
        }

        // Start Ongoing Mini Remote Notification Service
        val notifIntent = Intent(this, MiniRemoteNotificationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(notifIntent)
        } else {
            startService(notifIntent)
        }

        requestRequiredPermissions()
        handleNfcIntent(intent, macroViewModel)

        setContent {
            SmartRemoteProTheme {
                MainAppScaffold(
                    homeViewModel = homeViewModel,
                    remoteViewModel = remoteViewModel,
                    airtelViewModel = airtelViewModel,
                    macroViewModel = macroViewModel,
                    voiceViewModel = voiceViewModel,
                    profileViewModel = profileViewModel,
                    settingsViewModel = settingsViewModel,
                    learningManager = container.irLearningManager
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shakeDetector.start()
    }

    override fun onPause() {
        super.onPause()
        shakeDetector.stop()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val app = application as SmartRemoteApp
        val macroViewModel = MacroViewModel(
            macroRepository = app.appContainer.macroRepository,
            executeMacroUseCase = app.appContainer.executeMacroUseCase
        )
        handleNfcIntent(intent, macroViewModel)
    }

    private fun handleNfcIntent(intent: Intent?, macroViewModel: MacroViewModel) {
        val macroTrigger = NfcMacroHandler.extractMacroTrigger(intent)
        if (macroTrigger != null) {
            val app = application as SmartRemoteApp
            lifecycleScope.launch {
                val macro = app.appContainer.macroRepository.getMacroByVoiceTrigger(macroTrigger.lowercase())
                    ?: app.appContainer.macroRepository.getMacroByNfcTag(macroTrigger)
                if (macro != null) {
                    macroViewModel.runMacro(macro)
                }
            }
        }
    }

    private fun requestRequiredPermissions() {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.VIBRATE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val ungranted = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (ungranted.isNotEmpty()) {
            permissionLauncher.launch(ungranted.toTypedArray())
        }
    }
}
