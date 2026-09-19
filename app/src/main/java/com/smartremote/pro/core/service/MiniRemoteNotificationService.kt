package com.smartremote.pro.core.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.smartremote.pro.R
import com.smartremote.pro.SmartRemoteApp
import com.smartremote.pro.presentation.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MiniRemoteNotificationService : Service() {

    private val channelId = "mini_remote_notification_channel"
    private val notificationId = 1002

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action != null && action.startsWith("ACTION_CMD_")) {
            handleRemoteAction(action)
        }

        val notification = buildNotification()
        startForeground(notificationId, notification)

        return START_STICKY
    }

    private fun handleRemoteAction(action: String) {
        val cmd = when (action) {
            "ACTION_CMD_POWER" -> "power"
            "ACTION_CMD_VOL_UP" -> "volUp"
            "ACTION_CMD_VOL_DOWN" -> "volDown"
            "ACTION_CMD_MUTE" -> "mute"
            else -> return
        }

        val app = applicationContext as? SmartRemoteApp
        val irTransmitter = app?.appContainer?.irTransmitter
        val deviceRepo = app?.appContainer?.deviceRepository

        CoroutineScope(Dispatchers.IO).launch {
            val devices = deviceRepo?.discoverNetworkDevices()
            val primaryDevice = devices?.firstOrNull()
            if (primaryDevice != null && deviceRepo != null && irTransmitter != null) {
                val hex = deviceRepo.getPreloadedCommands(primaryDevice.brand, primaryDevice.type)[cmd]
                if (hex != null) {
                    irTransmitter.transmitHex(hex)
                }
            }
        }
    }

    private fun buildNotification(): Notification {
        val remoteViews = RemoteViews(packageName, R.layout.notification_mini_remote)

        // Pending Intents for controls
        remoteViews.setOnClickPendingIntent(R.id.btn_notif_power, createActionPendingIntent("ACTION_CMD_POWER", 1))
        remoteViews.setOnClickPendingIntent(R.id.btn_notif_vol_up, createActionPendingIntent("ACTION_CMD_VOL_UP", 2))
        remoteViews.setOnClickPendingIntent(R.id.btn_notif_vol_down, createActionPendingIntent("ACTION_CMD_VOL_DOWN", 3))
        remoteViews.setOnClickPendingIntent(R.id.btn_notif_mute, createActionPendingIntent("ACTION_CMD_MUTE", 4))

        val openAppIntent = Intent(this, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_remote_tile)
            .setCustomContentView(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createActionPendingIntent(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(this, MiniRemoteNotificationService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Mini Remote",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Mini remote control in the notification panel"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
