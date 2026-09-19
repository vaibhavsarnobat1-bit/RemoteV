package com.smartremote.pro.domain.models

sealed class VoiceCommandAction {
    object PowerToggle : VoiceCommandAction()
    object PowerOn : VoiceCommandAction()
    object PowerOff : VoiceCommandAction()
    object VolumeUp : VoiceCommandAction()
    object VolumeDown : VoiceCommandAction()
    data class SetVolume(val level: Int) : VoiceCommandAction()
    object MuteToggle : VoiceCommandAction()
    object ChannelUp : VoiceCommandAction()
    object ChannelDown : VoiceCommandAction()
    data class ChangeChannel(val channelNumber: Int) : VoiceCommandAction()
    data class OpenApp(val appName: String) : VoiceCommandAction()
    data class RunMacro(val macroName: String) : VoiceCommandAction()
    data class SetTemperature(val degrees: Int) : VoiceCommandAction()
    object PlayPause : VoiceCommandAction()
    object Unknown : VoiceCommandAction()
}

data class VoiceCommandResult(
    val rawQuery: String,
    val detectedLanguage: String, // "en" or "hi"
    val action: VoiceCommandAction,
    val targetDeviceType: DeviceType? = null,
    val feedbackMessage: String = "",
    val isSuccess: Boolean = true
)
