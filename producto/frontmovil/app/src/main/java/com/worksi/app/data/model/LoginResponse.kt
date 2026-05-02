package com.worksi.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String,
    @Json(name = "expires_in") val expiresIn: Int,
    @Json(name = "user") val user: UserInfo,
    @Json(name = "lock_info") val lockInfo: LockInfo
)

@JsonClass(generateAdapter = true)
data class UserInfo(
    @Json(name = "id") val id: Long,
    @Json(name = "role") val role: String,
    @Json(name = "email") val email: String
)