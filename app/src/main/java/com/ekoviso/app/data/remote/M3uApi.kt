package com.ekoviso.app.data.remote

import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object M3uApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun fetchPlaylist(url: String): Result<String> {
        return try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "EkoViso/1.0")
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success(response.body?.string() ?: "")
            } else {
                Result.failure(Exception("HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
