package com.worksi.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CandidateApplicationBody(
    @Json(name = "job_id") val jobId: Long
)

@JsonClass(generateAdapter = true)
data class CandidateApplicationResponse(
    @Json(name = "application_id") val applicationId: Long,
    val status: String,
    @Json(name = "applied_at") val appliedAt: String
)