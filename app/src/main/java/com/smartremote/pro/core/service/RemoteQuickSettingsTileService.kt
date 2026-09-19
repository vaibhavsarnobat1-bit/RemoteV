package com.smartremote.pro.core.service

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.smartremote.pro.SmartRemoteApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class RemoteQuickSettingsTileService : TileService() {

    override fun onClick() {
        super.onClick()

        val tile = qsTile ?: return
        val isNowActive = tile.state != Tile.STATE_ACTIVE

        tile.state = if (isNowActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.updateTile()

        val app = applicationContext as? SmartRemoteApp
        val irTransmitter = app?.appContainer?.irTransmitter
        val deviceRepo = app?.appContainer?.deviceRepository

        CoroutineScope(Dispatchers.IO).launch {
            val devices = deviceRepo?.discoverNetworkDevices()
            val primaryDevice = devices?.firstOrNull()
            if (primaryDevice != null && deviceRepo != null && irTransmitter != null) {
                val hex = deviceRepo.getPreloadedCommands(primaryDevice.brand, primaryDevice.type)["power"]
                if (hex != null) {
                    irTransmitter.transmitHex(hex)
                }
            }
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile ?: return
        tile.state = Tile.STATE_INACTIVE
        tile.label = "Smart Remote"
        tile.updateTile()
    }
}
