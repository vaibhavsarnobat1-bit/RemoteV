package com.smartremote.pro.core.macro

import android.util.Log
import com.smartremote.pro.core.ir.IRTransmitter
import com.smartremote.pro.core.network.SmartTvClient
import com.smartremote.pro.domain.models.ConnectionType
import com.smartremote.pro.domain.models.Macro
import com.smartremote.pro.domain.repository.DeviceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MacroExecutionEngine(
    private val deviceRepository: DeviceRepository,
    private val irTransmitter: IRTransmitter,
    private val smartTvClient: SmartTvClient
) {
    private val tag = "MacroEngine"

    suspend fun execute(
        macro: Macro,
        onStepProgress: ((stepIndex: Int, total: Int) -> Unit)? = null
    ): Boolean = withContext(Dispatchers.IO) {
        val steps = macro.steps.sortedBy { it.stepOrder }
        if (steps.isEmpty()) return@withContext true

        Log.d(tag, "Executing macro '${macro.name}' with ${steps.size} steps")

        steps.forEachIndexed { index, step ->
            onStepProgress?.invoke(index + 1, steps.size)

            val device = deviceRepository.getDeviceById(step.targetDeviceId)
            if (device != null) {
                try {
                    when (device.connectionType) {
                        ConnectionType.IR -> {
                            val hex = deviceRepository.getCommandForDevice(device.id, step.commandName)?.hexCode
                                ?: deviceRepository.getPreloadedCommands(device.brand, device.type)[step.commandName]
                            if (hex != null) {
                                irTransmitter.transmitHex(hex)
                            }
                        }
                        ConnectionType.WIFI, ConnectionType.HYBRID -> {
                            val ip = device.ipAddress
                            if (!ip.isNullOrBlank()) {
                                smartTvClient.sendCommand(ip, device.port, step.commandName, device.brand)
                            } else {
                                val hex = deviceRepository.getPreloadedCommands(device.brand, device.type)[step.commandName]
                                if (hex != null) irTransmitter.transmitHex(hex)
                            }
                        }
                        ConnectionType.BLUETOOTH -> {}
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Step ${step.stepOrder} error", e)
                }
            }

            if (step.delayAfterMs > 0) {
                delay(step.delayAfterMs)
            }
        }

        true
    }
}
