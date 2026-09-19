package com.smartremote.pro.core.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

sealed class VoiceState {
    object Idle : VoiceState()
    object Ready : VoiceState()
    data class Listening(val rmsDb: Float) : VoiceState()
    data class Partial(val text: String) : VoiceState()
    data class Final(val text: String) : VoiceState()
    data class Error(val message: String) : VoiceState()
}

class VoiceRecognizer(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    val isAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context)

    fun startListening(): Flow<VoiceState> = callbackFlow {
        if (!isAvailable) {
            trySend(VoiceState.Error("Speech Recognition is not supported on this device"))
            close()
            return@callbackFlow
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                trySend(VoiceState.Ready)
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {
                // Normalizes audio input level for waveform visualizer
                val normalized = (rmsdB + 2f).coerceIn(0f, 15f) / 15f
                trySend(VoiceState.Listening(normalized))
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                val msg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Record audio permission required"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Speech recognition error: $error"
                }
                trySend(VoiceState.Error(msg))
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val recognized = matches?.firstOrNull() ?: ""
                trySend(VoiceState.Final(recognized))
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partial = matches?.firstOrNull() ?: ""
                if (partial.isNotEmpty()) {
                    trySend(VoiceState.Partial(partial))
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        speechRecognizer?.setRecognitionListener(listener)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            // Support both English and Hindi locale hints
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-IN,hi-IN")
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            trySend(VoiceState.Error("Unable to start listening: ${e.message}"))
        }

        awaitClose {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
        }
    }

    fun stop() {
        speechRecognizer?.stopListening()
    }
}
