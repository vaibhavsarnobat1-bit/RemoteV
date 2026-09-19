package com.smartremote.pro

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.smartremote.pro.core.schedule.RechargeReminderWorker
import com.smartremote.pro.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SmartRemoteApp : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)

        // Asynchronously initialize database defaults
        CoroutineScope(Dispatchers.IO).launch {
            appContainer.profileRepository.initializeDefaultProfiles()
            appContainer.macroRepository.initializeDefaultMacros()
            appContainer.channelRepository.loadInitialChannels()
            schedulePeriodicRechargeChecks()
        }
    }

    private fun schedulePeriodicRechargeChecks() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<RechargeReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "recharge_reminder_check",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
