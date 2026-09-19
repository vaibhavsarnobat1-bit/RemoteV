package com.smartremote.pro.core.schedule

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smartremote.pro.SmartRemoteApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val tag = "ScheduleWorker"

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val macroId = inputData.getString(KEY_MACRO_ID) ?: return@withContext Result.failure()
        Log.d(tag, "Executing scheduled macro: $macroId")

        val app = applicationContext as? SmartRemoteApp
        val macroRepo = app?.appContainer?.macroRepository
        val engine = app?.appContainer?.macroExecutionEngine

        if (macroRepo != null && engine != null) {
            val macro = macroRepo.getMacroById(macroId)
            if (macro != null && macro.isEnabled) {
                engine.execute(macro)
                return@withContext Result.success()
            }
        }

        Result.failure()
    }

    companion object {
        const val KEY_MACRO_ID = "scheduled_macro_id"
    }
}
