package com.worksi.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LockInfo(
    @Json(name = "is_locked") val isLocked: Boolean,
    @Json(name = "failed_attempts") val failedAttempts: Int,
    @Json(name = "remaining_attempts") val remainingAttempts: Int
)
