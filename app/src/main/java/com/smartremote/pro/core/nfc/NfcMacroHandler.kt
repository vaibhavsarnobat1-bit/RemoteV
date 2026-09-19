package com.smartremote.pro.core.nfc

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter

object NfcMacroHandler {

    /**
     * Extracts macro identifier or payload from NFC NDEF intent.
     */
    fun extractMacroTrigger(intent: Intent?): String? {
        if (intent == null || NfcAdapter.ACTION_NDEF_DISCOVERED != intent.action) return null

        val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) ?: return null
        for (raw in rawMessages) {
            val message = raw as? NdefMessage ?: continue
            for (record in message.records) {
                try {
                    val payload = record.payload
                    // Text record payload: first byte is status byte (encoding & language code length)
                    val langCodeLength = (payload[0].toInt() and 0x3F)
                    val text = String(payload, langCodeLength + 1, payload.size - langCodeLength - 1, Charsets.UTF_8)
                    if (text.startsWith("macro:", ignoreCase = true)) {
                        return text.removePrefix("macro:").trim()
                    }
                    return text.trim()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }
}
