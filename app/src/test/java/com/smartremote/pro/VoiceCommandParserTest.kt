package com.smartremote.pro

import com.smartremote.pro.core.voice.VoiceCommandParser
import com.smartremote.pro.domain.models.DeviceType
import com.smartremote.pro.domain.models.VoiceCommandAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VoiceCommandParserTest {

    private lateinit var parser: VoiceCommandParser

    @Before
    fun setup() {
        parser = VoiceCommandParser()
    }

    @Test
    fun parse_englishPowerOn_returnsPowerOnAction() {
        val result = parser.parse("Turn on TV")
        assertTrue(result.isSuccess)
        assertEquals("en", result.detectedLanguage)
        assertEquals(VoiceCommandAction.PowerOn, result.action)
        assertEquals(DeviceType.TV, result.targetDeviceType)
    }

    @Test
    fun parse_hindiPowerOn_returnsPowerOnAction() {
        val result = parser.parse("TV chalu karo")
        assertTrue(result.isSuccess)
        assertEquals("hi", result.detectedLanguage)
        assertEquals(VoiceCommandAction.PowerOn, result.action)
        assertEquals(DeviceType.TV, result.targetDeviceType)
    }

    @Test
    fun parse_hindiVolumeBadao_returnsVolumeUp() {
        val result = parser.parse("Awaaz badao")
        assertTrue(result.isSuccess)
        assertEquals("hi", result.detectedLanguage)
        assertEquals(VoiceCommandAction.VolumeUp, result.action)
    }

    @Test
    fun parse_englishSetVolume25_extractsNumericParameter() {
        val result = parser.parse("Set volume to 25")
        assertTrue(result.isSuccess)
        assertEquals(VoiceCommandAction.SetVolume(25), result.action)
    }

    @Test
    fun parse_changeChannel100_returnsChangeChannelAction() {
        val result = parser.parse("Channel 100")
        assertTrue(result.isSuccess)
        assertEquals(VoiceCommandAction.ChangeChannel(100), result.action)
        assertEquals(DeviceType.SET_TOP_BOX, result.targetDeviceType)
    }

    @Test
    fun parse_hindiYoutubeKholo_returnsOpenAppAction() {
        val result = parser.parse("YouTube kholo")
        assertTrue(result.isSuccess)
        assertEquals("hi", result.detectedLanguage)
        assertEquals(VoiceCommandAction.OpenApp("YouTube"), result.action)
    }

    @Test
    fun parse_macroShortcutMovieTime_returnsRunMacroAction() {
        val result = parser.parse("Movie time")
        assertTrue(result.isSuccess)
        assertEquals(VoiceCommandAction.RunMacro("Movie Mode"), result.action)
    }
}
