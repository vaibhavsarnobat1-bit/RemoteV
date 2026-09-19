package com.smartremote.pro

import com.smartremote.pro.core.ir.IrProtocolEncoder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IrProtocolEncoderTest {

    @Test
    fun encodeNec_validHex_generatesExpectedPulseCount() {
        val pulses = IrProtocolEncoder.encodeNec("0x20DF10EF")
        // NEC frame: 2 leader + (32 * 2 data) + 1 stop = 67 pulse durations
        assertEquals(67, pulses.size)
        // Check leader mark and space
        assertEquals(9000, pulses[0])
        assertEquals(4500, pulses[1])
        // Check stop bit
        assertEquals(560, pulses[66])
    }

    @Test
    fun encodeSonySirc_12bit_generatesExpectedPulses() {
        val pulses = IrProtocolEncoder.encodeSonySirc("0xA90", bitCount = 12)
        // Leader 2 + (12 * 2) = 26 pulses
        assertEquals(26, pulses.size)
        assertEquals(2400, pulses[0])
        assertEquals(600, pulses[1])
    }

    @Test
    fun decodePronto_validProntoString_extractsFrequencyAndPulses() {
        val prontoHex = "0000 006D 0002 0000 0156 00AB 0015 0015"
        val result = IrProtocolEncoder.decodePronto(prontoHex)
        assertNotNull(result)
        val (freq, pulses) = result!!
        assertTrue(freq in 36000..40000)
        assertEquals(4, pulses.size)
    }
}
