package com.smartremote.pro.core.utils

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FindMyRemoteBeep {

    private var beepJob: Job? = null

    fun startBeeping(durationSeconds: Int = 10) {
        stopBeeping()
        beepJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)
                val endTime = System.currentTimeMillis() + (durationSeconds * 1000L)

                while (System.currentTimeMillis() < endTime && beepJob?.isActive == true) {
                    toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400)
                    delay(600L)
                }
                toneGen.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopBeeping() {
        beepJob?.cancel()
        beepJob = null
    }
}
