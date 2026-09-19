package com.smartremote.pro.core.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import android.util.Log

class IRTransmitter(private val context: Context) {

    private val tag = "IRTransmitter"
    private val irManager: ConsumerIrManager? =
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    val hasHardwareEmitter: Boolean
        get() = irManager?.hasIrEmitter() == true

    /**
     * Transmits raw pulse pattern at the specified carrier frequency.
     */
    fun transmit(frequency: Int, pattern: IntArray): Boolean {
        if (pattern.isEmpty()) return false

        return if (hasHardwareEmitter && irManager != null) {
            try {
                // Verify frequency range support
                val ranges = irManager.carrierFrequencies
                var isSupported = ranges.isNullOrEmpty()
                if (ranges != null) {
                    for (range in ranges) {
                        if (frequency in range.minFrequency..range.maxFrequency) {
                            isSupported = true
                            break
                        }
                    }
                }

                val actualFrequency = if (isSupported) frequency else IrProtocolEncoder.DEFAULT_NEC_FREQUENCY
                irManager.transmit(actualFrequency, pattern)
                Log.d(tag, "Transmitted IR pattern with ${pattern.size} pulses at ${actualFrequency}Hz")
                true
            } catch (e: Exception) {
                Log.e(tag, "Error transmitting IR", e)
                false
            }
        } else {
            // Simulated transmission for devices without hardware IR blaster
            Log.d(tag, "[SIMULATED IR] Emitted ${pattern.size} pulses at ${frequency}Hz (No hardware IR blaster)")
            true
        }
    }

    /**
     * Encodes a hex code via NEC or Sony protocol and transmits it.
     */
    fun transmitHex(hexCode: String, frequency: Int = IrProtocolEncoder.DEFAULT_NEC_FREQUENCY): Boolean {
        val pattern = if (frequency == IrProtocolEncoder.DEFAULT_SONY_FREQUENCY || hexCode.length <= 4) {
            IrProtocolEncoder.encodeSonySirc(hexCode)
        } else {
            IrProtocolEncoder.encodeNec(hexCode)
        }
        return transmit(frequency, pattern)
    }

    /**
     * Transmits a Pronto format raw string.
     */
    fun transmitPronto(prontoHex: String): Boolean {
        val decoded = IrProtocolEncoder.decodePronto(prontoHex) ?: return false
        return transmit(decoded.first, decoded.second)
    }
}
