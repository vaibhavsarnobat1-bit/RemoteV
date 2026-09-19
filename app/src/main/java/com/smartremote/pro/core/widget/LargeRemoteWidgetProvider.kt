package com.smartremote.pro.core.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.smartremote.pro.R
import com.smartremote.pro.SmartRemoteApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LargeRemoteWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action ?: return
        if (action.startsWith(ACTION_WIDGET_PREFIX)) {
            val cmd = action.removePrefix(ACTION_WIDGET_PREFIX)
            dispatchCommand(context, cmd)
        }
    }

    private fun dispatchCommand(context: Context, cmd: String) {
        val app = context.applicationContext as? SmartRemoteApp
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

    companion object {
        const val ACTION_WIDGET_PREFIX = "com.smartremote.pro.WIDGET_LARGE_"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_remote_large)

            views.setOnClickPendingIntent(R.id.widget_btn_power, getPendingIntent(context, "power", 301))
            views.setOnClickPendingIntent(R.id.widget_btn_source, getPendingIntent(context, "source", 302))
            views.setOnClickPendingIntent(R.id.widget_btn_up, getPendingIntent(context, "up", 303))
            views.setOnClickPendingIntent(R.id.widget_btn_down, getPendingIntent(context, "down", 304))
            views.setOnClickPendingIntent(R.id.widget_btn_left, getPendingIntent(context, "left", 305))
            views.setOnClickPendingIntent(R.id.widget_btn_right, getPendingIntent(context, "right", 306))
            views.setOnClickPendingIntent(R.id.widget_btn_ok, getPendingIntent(context, "ok", 307))
            views.setOnClickPendingIntent(R.id.widget_btn_vol_down, getPendingIntent(context, "volDown", 308))
            views.setOnClickPendingIntent(R.id.widget_btn_vol_up, getPendingIntent(context, "volUp", 309))
            views.setOnClickPendingIntent(R.id.widget_btn_ch_down, getPendingIntent(context, "chDown", 310))
            views.setOnClickPendingIntent(R.id.widget_btn_ch_up, getPendingIntent(context, "chUp", 311))
            views.setOnClickPendingIntent(R.id.widget_btn_mute, getPendingIntent(context, "mute", 312))

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getPendingIntent(context: Context, command: String, requestCode: Int): PendingIntent {
            val intent = Intent(context, LargeRemoteWidgetProvider::class.java).apply {
                action = ACTION_WIDGET_PREFIX + command
            }
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
