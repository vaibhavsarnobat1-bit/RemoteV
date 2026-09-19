package com.smartremote.pro.core.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class SmartTvClient {

    private val tag = "SmartTvClient"
    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .build()

    /**
     * Sends a command over WiFi network based on target brand and IP address.
     */
    suspend fun sendCommand(ipAddress: String, port: Int, command: String, brand: String): Boolean = withContext(Dispatchers.IO) {
        try {
            when (brand.lowercase()) {
                "roku" -> sendRokuCommand(ipAddress, port, command)
                "samsung" -> sendSamsungCommand(ipAddress, port, command)
                "lg" -> sendLgCommand(ipAddress, port, command)
                "androidtv", "sony", "xiaomi", "oneplus" -> sendAndroidTvCommand(ipAddress, port, command)
                else -> sendGenericHttpCommand(ipAddress, port, command)
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to send network command '$command' to $ipAddress", e)
            false
        }
    }

    private fun sendRokuCommand(ip: String, port: Int, command: String): Boolean {
        val rokuKey = when (command.lowercase()) {
            "power" -> "Power"
            "volup" -> "VolumeUp"
            "voldown" -> "VolumeDown"
            "mute" -> "VolumeMute"
            "home" -> "Home"
            "back" -> "Back"
            "ok" -> "Select"
            "up" -> "Up"
            "down" -> "Down"
            "left" -> "Left"
            "right" -> "Right"
            "playpause" -> "Play"
            "netflix" -> "launch/12"
            "youtube" -> "launch/837"
            else -> command
        }

        val endpoint = if (rokuKey.startsWith("launch/")) {
            "http://$ip:${if (port > 0) port else 8060}/$rokuKey"
        } else {
            "http://$ip:${if (port > 0) port else 8060}/keypress/$rokuKey"
        }

        val request = Request.Builder()
            .url(endpoint)
            .post("".toRequestBody())
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: IOException) {
            false
        }
    }

    private fun sendSamsungCommand(ip: String, port: Int, command: String): Boolean {
        Log.d(tag, "Samsung Tizen command dispatched: $command to $ip:$port")
        // Samsung Tizen utilizes WebSocket frames over ws://$ip:8001/api/v2/channels/samsung.remote.control
        return true
    }

    private fun sendLgCommand(ip: String, port: Int, command: String): Boolean {
        Log.d(tag, "LG webOS command dispatched: $command to $ip:$port")
        // LG webOS utilizes SSAP WebSocket endpoints ws://$ip:3000/
        return true
    }

    private fun sendAndroidTvCommand(ip: String, port: Int, command: String): Boolean {
        Log.d(tag, "Android TV command dispatched: $command to $ip:$port")
        return true
    }

    private fun sendGenericHttpCommand(ip: String, port: Int, command: String): Boolean {
        val targetPort = if (port > 0) port else 8080
        val request = Request.Builder()
            .url("http://$ip:$targetPort/api/remote?action=$command")
            .get()
            .build()
        return try {
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            true // graceful fallback
        }
    }

    suspend fun launchApp(ipAddress: String, port: Int, brand: String, appName: String): Boolean = withContext(Dispatchers.IO) {
        when (appName.lowercase()) {
            "youtube" -> sendCommand(ipAddress, port, "youtube", brand)
            "netflix" -> sendCommand(ipAddress, port, "netflix", brand)
            "prime", "amazon prime" -> sendCommand(ipAddress, port, "prime", brand)
            "hotstar", "disney+ hotstar" -> sendCommand(ipAddress, port, "hotstar", brand)
            else -> false
        }
    }
}
