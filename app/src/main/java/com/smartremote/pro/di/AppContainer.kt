package com.smartremote.pro.di

import android.content.Context
import com.smartremote.pro.core.ir.IRCodeDatabase
import com.smartremote.pro.core.ir.IRLearningManager
import com.smartremote.pro.core.ir.IRTransmitter
import com.smartremote.pro.core.macro.MacroExecutionEngine
import com.smartremote.pro.core.network.DeviceDiscovery
import com.smartremote.pro.core.network.DlnaController
import com.smartremote.pro.core.network.SmartTvClient
import com.smartremote.pro.core.utils.FindMyRemoteBeep
import com.smartremote.pro.core.utils.HapticFeedbackManager
import com.smartremote.pro.core.voice.TextToSpeechManager
import com.smartremote.pro.core.voice.VoiceCommandParser
import com.smartremote.pro.core.voice.VoiceRecognizer
import com.smartremote.pro.data.local.database.AppDatabase
import com.smartremote.pro.data.local.preferences.EncryptedPreferenceManager
import com.smartremote.pro.data.repository.ChannelRepositoryImpl
import com.smartremote.pro.data.repository.DeviceRepositoryImpl
import com.smartremote.pro.data.repository.MacroRepositoryImpl
import com.smartremote.pro.data.repository.ProfileRepositoryImpl
import com.smartremote.pro.data.repository.SettingsRepositoryImpl
import com.smartremote.pro.domain.repository.ChannelRepository
import com.smartremote.pro.domain.repository.DeviceRepository
import com.smartremote.pro.domain.repository.MacroRepository
import com.smartremote.pro.domain.repository.ProfileRepository
import com.smartremote.pro.domain.usecases.DiscoverNetworkDevicesUseCase
import com.smartremote.pro.domain.usecases.ExecuteMacroUseCase
import com.smartremote.pro.domain.usecases.ParseVoiceCommandUseCase
import com.smartremote.pro.domain.usecases.SendDeviceCommandUseCase

class AppContainer(private val context: Context) {

    // Database & Preferences
    val database: AppDatabase by lazy { AppDatabase.getDatabase(context) }
    val preferenceManager: EncryptedPreferenceManager by lazy { EncryptedPreferenceManager(context) }

    // Core Hardware & Network Engines
    val irTransmitter: IRTransmitter by lazy { IRTransmitter(context) }
    val irCodeDatabase: IRCodeDatabase by lazy { IRCodeDatabase(context) }
    val irLearningManager: IRLearningManager by lazy { IRLearningManager() }
    val smartTvClient: SmartTvClient by lazy { SmartTvClient() }
    val deviceDiscovery: DeviceDiscovery by lazy { DeviceDiscovery(context) }
    val dlnaController: DlnaController by lazy { DlnaController() }

    // Audio & Voice Engines
    val voiceRecognizer: VoiceRecognizer by lazy { VoiceRecognizer(context) }
    val voiceCommandParser: VoiceCommandParser by lazy { VoiceCommandParser() }
    val textToSpeechManager: TextToSpeechManager by lazy { TextToSpeechManager(context) }

    // Utils & Gestures
    val hapticFeedbackManager: HapticFeedbackManager by lazy { HapticFeedbackManager(context) }
    val findMyRemoteBeep: FindMyRemoteBeep by lazy { FindMyRemoteBeep() }

    // Repositories
    val deviceRepository: DeviceRepository by lazy {
        DeviceRepositoryImpl(database.deviceDao(), irCodeDatabase, deviceDiscovery)
    }

    val macroRepository: MacroRepository by lazy {
        MacroRepositoryImpl(database.macroDao())
    }

    val channelRepository: ChannelRepository by lazy {
        ChannelRepositoryImpl(context, database.channelDao())
    }

    val profileRepository: ProfileRepository by lazy {
        ProfileRepositoryImpl(database.profileDao(), preferenceManager)
    }

    val settingsRepository: SettingsRepositoryImpl by lazy {
        SettingsRepositoryImpl(preferenceManager)
    }

    // Macro Engine
    val macroExecutionEngine: MacroExecutionEngine by lazy {
        MacroExecutionEngine(deviceRepository, irTransmitter, smartTvClient)
    }

    // Use Cases
    val sendDeviceCommandUseCase: SendDeviceCommandUseCase by lazy {
        SendDeviceCommandUseCase(deviceRepository, irTransmitter, smartTvClient, hapticFeedbackManager)
    }

    val executeMacroUseCase: ExecuteMacroUseCase by lazy {
        ExecuteMacroUseCase(macroExecutionEngine)
    }

    val parseVoiceCommandUseCase: ParseVoiceCommandUseCase by lazy {
        ParseVoiceCommandUseCase(voiceCommandParser)
    }

    val discoverNetworkDevicesUseCase: DiscoverNetworkDevicesUseCase by lazy {
        DiscoverNetworkDevicesUseCase(deviceRepository)
    }
}
