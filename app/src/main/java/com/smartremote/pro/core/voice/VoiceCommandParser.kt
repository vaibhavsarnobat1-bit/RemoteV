package com.smartremote.pro.core.voice

import com.smartremote.pro.domain.models.DeviceType
import com.smartremote.pro.domain.models.VoiceCommandAction
import com.smartremote.pro.domain.models.VoiceCommandResult

class VoiceCommandParser {

    /**
     * Parses spoken text in either English or Hindi (transliterated or Devanagari)
     * and produces structured action, target device, and spoken response text.
     */
    fun parse(rawText: String): VoiceCommandResult {
        val query = rawText.trim().lowercase()
        if (query.isEmpty()) {
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = "en",
                action = VoiceCommandAction.Unknown,
                feedbackMessage = "Could not hear any command",
                isSuccess = false
            )
        }

        val isHindi = isHindiQuery(query)
        val lang = if (isHindi) "hi" else "en"

        // Determine target device type if mentioned
        val targetDevice = detectDeviceType(query)

        // Extract any numeric parameters (e.g. "Channel 100", "Volume 25", "24 degrees")
        val extractedNumber = extractNumber(query)

        // 1. Power Commands
        if (matchesAny(query, listOf("turn on", "switch on", "power on", "chalu karo", "chalu", "on karo", "chalu kijiye", "चालू करो", "ऑन करो"))) {
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = lang,
                action = VoiceCommandAction.PowerOn,
                targetDeviceType = targetDevice ?: DeviceType.TV,
                feedbackMessage = if (isHindi) "TV chalu kar diya gaya hai" else "Turning on the device"
            )
        }

        if (matchesAny(query, listOf("turn off", "switch off", "power off", "band karo", "band", "off karo", "band kijiye", "बंद करो", "ऑफ करो"))) {
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = lang,
                action = VoiceCommandAction.PowerOff,
                targetDeviceType = targetDevice ?: DeviceType.TV,
                feedbackMessage = if (isHindi) "TV band kar diya gaya hai" else "Turning off the device"
            )
        }

        // 2. Volume Commands
        if (matchesAny(query, listOf("mute", "unmute", "shant", "awaaz band", "आवाज़ बंद", "म्यूट"))) {
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = lang,
                action = VoiceCommandAction.MuteToggle,
                targetDeviceType = targetDevice ?: DeviceType.TV,
                feedbackMessage = if (isHindi) "Mute toggle kar diya" else "Toggled mute"
            )
        }

        if (extractedNumber != null && matchesAny(query, listOf("volume", "awaaz", "sound", "आवाज़"))) {
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = lang,
                action = VoiceCommandAction.SetVolume(extractedNumber),
                targetDeviceType = targetDevice ?: DeviceType.TV,
                feedbackMessage = if (isHindi) "Volume $extractedNumber par set kar diya" else "Setting volume to $extractedNumber"
            )
        }

        if (matchesAny(query, listOf("volume up", "increase volume", "louder", "volume badao", "awaaz badao", "awaaz badhao", "badao", "आवाज़ बढ़ाओ"))) {
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = lang,
                action = VoiceCommandAction.VolumeUp,
                targetDeviceType = targetDevice ?: DeviceType.TV,
                feedbackMessage = if (isHindi) "Volume bada diya" else "Increasing volume"
            )
        }

        if (matchesAny(query, listOf("volume down", "decrease volume", "lower volume", "volume kam karo", "awaaz kam karo", "kam karo", "आवाज़ कम करो"))) {
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = lang,
                action = VoiceCommandAction.VolumeDown,
                targetDeviceType = targetDevice ?: DeviceType.TV,
                feedbackMessage = if (isHindi) "Volume kam kar diya" else "Decreasing volume"
            )
        }

        // 3. Channel Commands
        if (extractedNumber != null && matchesAny(query, listOf("channel", "ch", "chanel", "चैनल"))) {
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = lang,
                action = VoiceCommandAction.ChangeChannel(extractedNumber),
                targetDeviceType = targetDevice ?: DeviceType.SET_TOP_BOX,
                feedbackMessage = if (isHindi) "Channel $extractedNumber laga diya" else "Changing to channel $extractedNumber"
            )
        }

        if (matchesAny(query, listOf("channel up", "next channel", "channel aage", "अगला चैनल", "channel badlo"))) {
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = lang,
                action = VoiceCommandAction.ChannelUp,
                targetDeviceType = targetDevice ?: DeviceType.SET_TOP_BOX,
                feedbackMessage = if (isHindi) "Agla channel laga diya" else "Channel up"
            )
        }

        if (matchesAny(query, listOf("channel down", "previous channel", "channel peeche", "पिछला चैनल"))) {
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = lang,
                action = VoiceCommandAction.ChannelDown,
                targetDeviceType = targetDevice ?: DeviceType.SET_TOP_BOX,
                feedbackMessage = if (isHindi) "Pichhla channel laga diya" else "Channel down"
            )
        }

        // 4. App Launchers
        if (matchesAny(query, listOf("youtube", "netflix", "prime", "hotstar", "kholo", "open", "launch", "chalao"))) {
            val appName = when {
                query.contains("youtube") || query.contains("यूट्यूब") -> "YouTube"
                query.contains("netflix") || query.contains("नेटफ्लिक्स") -> "Netflix"
                query.contains("prime") || query.contains("amazon") -> "Prime Video"
                query.contains("hotstar") || query.contains("disney") -> "Hotstar"
                else -> null
            }

            if (appName != null) {
                return VoiceCommandResult(
                    rawQuery = rawText,
                    detectedLanguage = lang,
                    action = VoiceCommandAction.OpenApp(appName),
                    targetDeviceType = DeviceType.TV,
                    feedbackMessage = if (isHindi) "$appName khol diya gaya hai" else "Opening $appName"
                )
            }
        }

        // 5. AC Climate Commands
        if (targetDevice == DeviceType.AC || matchesAny(query, listOf("ac", "cooling", "temperature", "tapman", "डिग्री"))) {
            val degrees = extractedNumber ?: 24
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = lang,
                action = VoiceCommandAction.SetTemperature(degrees),
                targetDeviceType = DeviceType.AC,
                feedbackMessage = if (isHindi) "AC ka taapmaan $degrees degree set kiya" else "Setting AC temperature to $degrees°C"
            )
        }

        // 6. Pre-made & Custom Macro Shortcuts
        if (matchesAny(query, listOf("movie time", "film time", "cinema mode", "सिनेमा"))) {
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = lang,
                action = VoiceCommandAction.RunMacro("Movie Mode"),
                feedbackMessage = if (isHindi) "Movie mode shuru kiya" else "Activating Movie Mode"
            )
        }

        if (matchesAny(query, listOf("cricket mode", "match time", "sports mode", "क्रिकेट"))) {
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = lang,
                action = VoiceCommandAction.RunMacro("Cricket Mode"),
                feedbackMessage = if (isHindi) "Cricket mode chalu kiya" else "Activating Cricket Mode"
            )
        }

        if (matchesAny(query, listOf("sleep", "so jao", "good night", "night mode", "सो जाओ"))) {
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = lang,
                action = VoiceCommandAction.RunMacro("Night Mode"),
                feedbackMessage = if (isHindi) "Night mode activate kar diya" else "Activating Night Mode"
            )
        }

        // 7. Media Playback Toggle
        if (matchesAny(query, listOf("play", "pause", "rok do", "chalao", "rok", "प्ले", "पॉज"))) {
            return VoiceCommandResult(
                rawQuery = rawText,
                detectedLanguage = lang,
                action = VoiceCommandAction.PlayPause,
                targetDeviceType = targetDevice ?: DeviceType.TV,
                feedbackMessage = if (isHindi) "Playback toggle kiya" else "Toggled playback"
            )
        }

        return VoiceCommandResult(
            rawQuery = rawText,
            detectedLanguage = lang,
            action = VoiceCommandAction.Unknown,
            feedbackMessage = if (isHindi) "Kshama karein, aadesh samajh nahi aaya" else "Sorry, command not recognized",
            isSuccess = false
        )
    }

    private fun isHindiQuery(query: String): Boolean {
        val hindiKeywords = listOf(
            "karo", "kijiye", "chalu", "band", "badao", "badhao", "kam", "awaaz",
            "kholo", "chalao", "badlo", "shant", "pichhla", "agla", "rok", "taapmaan"
        )
        // Check for Devanagari Unicode block (0x0900 to 0x097F)
        val hasDevanagari = query.any { it.code in 0x0900..0x097F }
        if (hasDevanagari) return true

        return hindiKeywords.any { query.contains(it) }
    }

    private fun detectDeviceType(query: String): DeviceType? {
        return when {
            query.contains("tv") || query.contains("television") || query.contains("टीवी") -> DeviceType.TV
            query.contains("ac") || query.contains("air conditioner") || query.contains("cooler") || query.contains("एसी") -> DeviceType.AC
            query.contains("box") || query.contains("dth") || query.contains("airtel") || query.contains("tata") || query.contains("dish") -> DeviceType.SET_TOP_BOX
            query.contains("speaker") || query.contains("soundbar") || query.contains("sound") || query.contains("audio") -> DeviceType.SOUNDBAR
            else -> null
        }
    }

    private fun extractNumber(query: String): Int? {
        val regex = "\\b\\d{1,4}\\b".toRegex()
        val match = regex.find(query)
        if (match != null) {
            return match.value.toIntOrNull()
        }
        // Basic word-to-number mapping for Hindi/English
        val wordMap = mapOf(
            "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5,
            "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9, "ten" to 10,
            "ek" to 1, "do" to 2, "teen" to 3, "char" to 4, "paanch" to 5,
            "chhah" to 6, "saat" to 7, "aath" to 8, "nau" to 9, "das" to 10,
            "बीस" to 20, "पच्चीस" to 25
        )
        for ((word, value) in wordMap) {
            if (query.contains("\\b$word\\b".toRegex())) {
                return value
            }
        }
        return null
    }

    private fun matchesAny(text: String, keywords: List<String>): Boolean {
        return keywords.any { text.contains(it) }
    }
}
