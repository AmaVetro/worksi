package com.worksi.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiErrorEnvelope(
    @Json(name = "error") val error: ApiErrorDetail
)

@JsonClass(generateAdapter = true)
data class ApiErrorDetail(
    @Json(name = "code") val code: String,
    @Json(name = "message") val message: String
)
