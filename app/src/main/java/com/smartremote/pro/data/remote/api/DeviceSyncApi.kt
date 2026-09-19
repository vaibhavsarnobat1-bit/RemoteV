package com.smartremote.pro.data.remote.api

import com.smartremote.pro.data.remote.models.CloudBackupPayload
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DeviceSyncApi {

    @POST("api/v1/sync/backup")
    suspend fun uploadBackup(@Body payload: CloudBackupPayload): Response<Unit>

    @GET("api/v1/sync/backup/{userId}")
    suspend fun downloadBackup(@Path("userId") userId: String): Response<CloudBackupPayload>

    @GET("api/v1/epg/airtel/today")
    suspend fun getEpgSchedule(): Response<Map<String, String>>
}
