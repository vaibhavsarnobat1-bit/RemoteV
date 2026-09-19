package com.smartremote.pro.core.ir

import kotlin.math.roundToInt

object IrProtocolEncoder {

    const val DEFAULT_NEC_FREQUENCY = 38000
    const val DEFAULT_RC5_FREQUENCY = 36000
    const val DEFAULT_SONY_FREQUENCY = 40000

    /**
     * Converts a 32-bit NEC hex code (e.g., "0x20DF10EF" or "20DF10EF") into carrier pulses (microsecond intervals).
     */
    fun encodeNec(hexCode: String): IntArray {
        val cleanHex = hexCode.removePrefix("0x").removePrefix("0X")
        val codeValue = cleanHex.toLongOrNull(16) ?: 0L

        val pulses = mutableListOf<Int>()

        // 1. Leader code: 9000us mark, 4500us space
        pulses.add(9000)
        pulses.add(4500)

        // 2. Data: 32 bits, LSB first in standard NEC
        for (i in 0 until 32) {
            val bit = (codeValue shr (31 - i)) and 1L
            pulses.add(560) // bit mark
            if (bit == 1L) {
                pulses.add(1690) // logical 1 space
            } else {
                pulses.add(560) // logical 0 space
            }
        }

        // 3. Stop bit: 560us mark
        pulses.add(560)

        return pulses.toIntArray()
    }

    /**
     * Converts a Sony SIRC (12-bit / 15-bit / 20-bit) hex code into microsecond pulse intervals at 40kHz.
     */
    fun encodeSonySirc(hexCode: String, bitCount: Int = 12): IntArray {
        val cleanHex = hexCode.removePrefix("0x").removePrefix("0X")
        val codeValue = cleanHex.toLongOrNull(16) ?: 0L

        val pulses = mutableListOf<Int>()

        // Leader: 2400us mark, 600us space
        pulses.add(2400)
        pulses.add(600)

        for (i in 0 until bitCount) {
            val bit = (codeValue shr i) and 1L
            if (bit == 1L) {
                pulses.add(1200) // 1 mark
            } else {
                pulses.add(600) // 0 mark
            }
            pulses.add(600) // space
        }

        return pulses.toIntArray()
    }

    /**
     * Converts Philips RC5 protocol command into bi-phase pulses.
     */
    fun encodeRc5(command: Int, address: Int, toggle: Boolean = false): IntArray {
        val pulses = mutableListOf<Int>()
        val halfBit = 889 // microseconds

        // RC5 frame: 2 start bits (1, 1), 1 toggle bit, 5 address bits, 6 command bits = 14 bits
        var frame = (1 shl 13) or (1 shl 12)
        if (toggle) frame = frame or (1 shl 11)
        frame = frame or ((address and 0x1F) shl 6)
        frame = frame or (command and 0x3F)

        for (i in 13 downTo 0) {
            val bit = (frame shr i) and 1
            if (bit == 1) {
                // Bi-phase: space then mark
                pulses.add(halfBit)
                pulses.add(halfBit)
            } else {
                // Bi-phase: mark then space
                pulses.add(halfBit)
                pulses.add(halfBit)
            }
        }

        return pulses.toIntArray()
    }

    /**
     * Decodes standard Pronto Hex string into frequency and pulse array.
     */
    fun decodePronto(prontoHex: String): Pair<Int, IntArray>? {
        val tokens = prontoHex.trim().split("\\s+".toRegex())
        if (tokens.size < 4) return null

        val format = tokens[0].toIntOrNull(16) ?: return null
        if (format != 0) return null // Only raw learned codes (format 0000)

        val freqCode = tokens[1].toIntOrNull(16) ?: return null
        val frequency = if (freqCode > 0) (1000000.0 / (freqCode * 0.241246)).roundToInt() else 38000

        val burst1Pairs = tokens[2].toIntOrNull(16) ?: 0
        val burst2Pairs = tokens[3].toIntOrNull(16) ?: 0
        val totalPairs = burst1Pairs + burst2Pairs

        val timeBaseUs = 1000000.0 / frequency
        val pulses = mutableListOf<Int>()

        var index = 4
        for (i in 0 until totalPairs) {
            if (index + 1 >= tokens.size) break
            val markCount = tokens[index++].toIntOrNull(16) ?: 0
            val spaceCount = tokens[index++].toIntOrNull(16) ?: 0
            pulses.add((markCount * timeBaseUs).roundToInt())
            pulses.add((spaceCount * timeBaseUs).roundToInt())
        }

        return Pair(frequency, pulses.toIntArray())
    }
}
