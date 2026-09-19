package com.smartremote.pro.domain.usecases

import com.smartremote.pro.core.voice.VoiceCommandParser
import com.smartremote.pro.domain.models.VoiceCommandResult

class ParseVoiceCommandUseCase(
    private val voiceCommandParser: VoiceCommandParser
) {
    operator fun invoke(rawSpeech: String): VoiceCommandResult {
        return voiceCommandParser.parse(rawSpeech)
    }
}
