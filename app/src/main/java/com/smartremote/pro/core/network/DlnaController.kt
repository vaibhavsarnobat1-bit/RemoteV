package com.smartremote.pro.core.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class DlnaController {

    private val tag = "DlnaController"
    private val client = OkHttpClient()
    private val xmlMediaType = "text/xml; charset=\"utf-8\"".toMediaType()

    suspend fun play(controlUrl: String): Boolean = sendSoapAction(controlUrl, "Play", "<Speed>1</Speed>")

    suspend fun pause(controlUrl: String): Boolean = sendSoapAction(controlUrl, "Pause", "")

    suspend fun stop(controlUrl: String): Boolean = sendSoapAction(controlUrl, "Stop", "")

    suspend fun setVolume(controlUrl: String, volume: Int): Boolean {
        val body = "<Channel>Master</Channel><DesiredVolume>$volume</DesiredVolume>"
        return sendSoapAction(controlUrl, "SetVolume", body, serviceType = "RenderingControl")
    }

    private suspend fun sendSoapAction(
        controlUrl: String,
        action: String,
        bodyArguments: String,
        serviceType: String = "AVTransport"
    ): Boolean = withContext(Dispatchers.IO) {
        val soapPayload = """
            <?xml version="1.0" encoding="utf-8"?>
            <s:Envelope s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
               <s:Body>
                  <u:$action xmlns:u="urn:schemas-upnp-org:service:$serviceType:1">
                     <InstanceID>0</InstanceID>
                     $bodyArguments
                  </u:$action>
               </s:Body>
            </s:Envelope>
        """.trimIndent()

        val request = Request.Builder()
            .url(controlUrl)
            .addHeader("SOAPAction", "\"urn:schemas-upnp-org:service:$serviceType:1#$action\"")
            .post(soapPayload.toRequestBody(xmlMediaType))
            .build()

        try {
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(tag, "DLNA SOAP action $action failed", e)
            false
        }
    }
}
