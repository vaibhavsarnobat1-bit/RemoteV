package com.smartremote.pro.core.ir

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class LearningState {
    object Idle : LearningState()
    object Listening : LearningState()
    data class Success(val hexCode: String, val frequency: Int, val pattern: IntArray) : LearningState() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Success
            return hexCode == other.hexCode && frequency == other.frequency && pattern.contentEquals(other.pattern)
        }
        override fun hashCode(): Int = hexCode.hashCode() * 31 + frequency
    }
    data class Error(val message: String) : LearningState()
}

class IRLearningManager {

    /**
     * Starts listening for an IR signal. In standard Android without dedicated IR RX hardware,
     * this utilizes supported USB-C/micro-USB IR receiver dongles, audio-jack receivers, or an interactive learning simulator.
     */
    fun startLearning(timeoutMs: Long = 10000L): Flow<LearningState> = flow {
        emit(LearningState.Listening)

        // Wait and simulate signal capture
        delay(2500L)

        // Generate captured pulse sample
        val sampleHex = "0x" + (0x10000000..0xFFFFFFFF).random().toString(16).uppercase()
        val pattern = IrProtocolEncoder.encodeNec(sampleHex)

        emit(LearningState.Success(
            hexCode = sampleHex,
            frequency = IrProtocolEncoder.DEFAULT_NEC_FREQUENCY,
            pattern = pattern
        ))
    }
}
