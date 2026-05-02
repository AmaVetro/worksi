package com.worksi.app.data.api

import com.worksi.app.data.model.ApiErrorEnvelope
import retrofit2.Response

object ApiErrorParser {
    private val adapter = RetrofitClient.moshi.adapter(ApiErrorEnvelope::class.java)

    fun message(response: Response<*>): String {
        val raw = response.errorBody()?.string() ?: return "Error"
        return try {
            adapter.fromJson(raw)?.error?.message ?: "Error"
        } catch (e: Exception) {
            "Error"
        }
    }
}
