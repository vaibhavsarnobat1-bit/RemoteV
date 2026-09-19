package com.smartremote.pro.core.schedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smartremote.pro.R
import com.smartremote.pro.presentation.ui.MainActivity

class RechargeReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val channelId = "dth_recharge_reminders"

    override suspend fun doWork(): Result {
        val daysLeft = inputData.getInt(KEY_DAYS_LEFT, 3)
        val balanceAmount = inputData.getString(KEY_BALANCE) ?: "₹49.00"

        createNotificationChannel()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_TAB", "AIRTEL")
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_remote_tile)
            .setContentTitle("Airtel DTH Recharge Reminder")
            .setContentText("Your Airtel Xstream pack expires in $daysLeft days. Balance: $balanceAmount")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(1001, notification)

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recharge Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifies about Airtel Xstream recharge expiry"
            }
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val KEY_DAYS_LEFT = "recharge_days_left"
        const val KEY_BALANCE = "account_balance"
    }
}
