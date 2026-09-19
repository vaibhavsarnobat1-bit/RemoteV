package com.smartremote.pro.core.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.language = Locale.ENGLISH
        }
    }

    fun speak(text: String, languageCode: String = "en") {
        if (!isInitialized || tts == null) return

        val locale = if (languageCode.equals("hi", ignoreCase = true)) {
            Locale("hi", "IN")
        } else {
            Locale.ENGLISH
        }

        tts?.language = locale
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "smart_remote_tts")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
