package com.smartremote.pro.domain.usecases

import com.smartremote.pro.core.ir.IRTransmitter
import com.smartremote.pro.core.network.SmartTvClient
import com.smartremote.pro.core.utils.HapticFeedbackManager
import com.smartremote.pro.domain.models.ConnectionType
import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.domain.models.RemoteCommand
import com.smartremote.pro.domain.repository.DeviceRepository

class SendDeviceCommandUseCase(
    private val deviceRepository: DeviceRepository,
    private val irTransmitter: IRTransmitter,
    private val smartTvClient: SmartTvClient,
    private val hapticFeedbackManager: HapticFeedbackManager
) {
    suspend operator fun invoke(device: Device, commandName: String): Result<Boolean> {
        hapticFeedbackManager.performClickFeedback()
        val command = deviceRepository.getCommandForDevice(device.id, commandName)

        return when (device.connectionType) {
            ConnectionType.IR -> {
                if (command?.pattern != null) {
                    val success = irTransmitter.transmit(command.frequency, command.pattern)
                    Result.success(success)
                } else if (command?.hexCode != null) {
                    val success = irTransmitter.transmitHex(command.hexCode, command.frequency)
                    Result.success(success)
                } else {
                    // Fallback to pre-loaded brand database lookup
                    val codes = deviceRepository.getPreloadedCommands(device.brand, device.type)
                    val hex = codes[commandName]
                    if (hex != null) {
                        val success = irTransmitter.transmitHex(hex)
                        Result.success(success)
                    } else {
                        Result.failure(IllegalArgumentException("Command '$commandName' not found for ${device.brand}"))
                    }
                }
            }
            ConnectionType.WIFI -> {
                val ip = device.ipAddress ?: return Result.failure(IllegalStateException("No IP address configured"))
                val success = smartTvClient.sendCommand(ip, device.port, commandName, device.brand)
                Result.success(success)
            }
            ConnectionType.HYBRID -> {
                // Try WiFi first; if unavailable, fallback to IR
                var success = false
                if (!device.ipAddress.isNullOrBlank()) {
                    success = smartTvClient.sendCommand(device.ipAddress, device.port, commandName, device.brand)
                }
                if (!success) {
                    val hex = command?.hexCode ?: deviceRepository.getPreloadedCommands(device.brand, device.type)[commandName]
                    if (hex != null) {
                        success = irTransmitter.transmitHex(hex)
                    }
                }
                Result.success(success)
            }
            ConnectionType.BLUETOOTH -> {
                Result.success(true)
            }
        }
    }
}
