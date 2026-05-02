package com.worksi.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PasswordRecoveryRequestBody(
    @Json(name = "email") val email: String
)

@JsonClass(generateAdapter = true)
data class PasswordRecoveryRequestResponse(
    @Json(name = "message") val message: String,
    @Json(name = "mock_flow") val mockFlow: Boolean
)

@JsonClass(generateAdapter = true)
data class PasswordRecoveryVerifyBody(
    @Json(name = "email") val email: String,
    @Json(name = "code") val code: String
)

@JsonClass(generateAdapter = true)
data class PasswordRecoveryVerifyResponse(
    @Json(name = "verified") val verified: Boolean,
    @Json(name = "recovery_token") val recoveryToken: String
)

@JsonClass(generateAdapter = true)
data class PasswordRecoveryResetBody(
    @Json(name = "email") val email: String,
    @Json(name = "recovery_token") val recoveryToken: String,
    @Json(name = "new_password") val newPassword: String
)

@JsonClass(generateAdapter = true)
data class PasswordRecoveryResetResponse(
    @Json(name = "message") val message: String
)
