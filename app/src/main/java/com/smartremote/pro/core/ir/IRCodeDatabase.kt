package com.smartremote.pro.core.ir

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.smartremote.pro.domain.models.DeviceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class IRCodeDatabase(private val context: Context) {

    private var cachedDatabase: JsonObject? = null

    suspend fun getCommandCode(brand: String, deviceType: DeviceType, commandKey: String): String? {
        val db = getOrLoadDatabase() ?: return null
        return try {
            val brands = db.getAsJsonObject("brands")
            val brandObj = brands?.getAsJsonObject(brand) ?: return null
            val typeKey = when (deviceType) {
                DeviceType.TV -> "TV"
                DeviceType.AC -> "AC"
                DeviceType.SET_TOP_BOX -> if (brandObj.has("Xstream")) "Xstream" else "SetTopBox"
                DeviceType.SOUNDBAR -> "Soundbar"
                DeviceType.DVD -> "DVD"
                DeviceType.PROJECTOR -> "Projector"
            }
            val deviceObj = brandObj.getAsJsonObject(typeKey) ?: return null
            deviceObj.get(commandKey)?.asString
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllCommands(brand: String, deviceType: DeviceType): Map<String, String> {
        val db = getOrLoadDatabase() ?: return emptyMap()
        val result = mutableMapOf<String, String>()
        try {
            val brands = db.getAsJsonObject("brands")
            val brandObj = brands?.getAsJsonObject(brand) ?: return emptyMap()
            val typeKey = when (deviceType) {
                DeviceType.TV -> "TV"
                DeviceType.AC -> "AC"
                DeviceType.SET_TOP_BOX -> if (brandObj.has("Xstream")) "Xstream" else "SetTopBox"
                DeviceType.SOUNDBAR -> "Soundbar"
                DeviceType.DVD -> "DVD"
                DeviceType.PROJECTOR -> "Projector"
            }
            val deviceObj = brandObj.getAsJsonObject(typeKey) ?: return emptyMap()
            for (entry in deviceObj.entrySet()) {
                if (entry.value.isJsonPrimitive) {
                    result[entry.key] = entry.value.asString
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    suspend fun getSupportedBrands(): List<String> {
        val db = getOrLoadDatabase() ?: return emptyList()
        return try {
            val brands = db.getAsJsonObject("brands")
            brands?.keySet()?.toList()?.sorted() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun getOrLoadDatabase(): JsonObject? = withContext(Dispatchers.IO) {
        if (cachedDatabase != null) return@withContext cachedDatabase
        try {
            context.assets.open("ir_codes/ir_database.json").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    cachedDatabase = Gson().fromJson(reader, JsonObject::class.java)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        cachedDatabase
    }
}
