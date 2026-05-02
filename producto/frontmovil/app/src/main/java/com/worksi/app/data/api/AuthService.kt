package com.worksi.app.data.api

import com.worksi.app.data.model.LoginRequest
import com.worksi.app.data.model.LoginResponse
import com.worksi.app.data.model.PasswordRecoveryRequestBody
import com.worksi.app.data.model.PasswordRecoveryRequestResponse
import com.worksi.app.data.model.PasswordRecoveryResetBody
import com.worksi.app.data.model.PasswordRecoveryResetResponse
import com.worksi.app.data.model.PasswordRecoveryVerifyBody
import com.worksi.app.data.model.PasswordRecoveryVerifyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/v1/auth/password-recovery/request")
    suspend fun passwordRecoveryRequest(
        @Body body: PasswordRecoveryRequestBody
    ): Response<PasswordRecoveryRequestResponse>

    @POST("api/v1/auth/password-recovery/verify")
    suspend fun passwordRecoveryVerify(
        @Body body: PasswordRecoveryVerifyBody
    ): Response<PasswordRecoveryVerifyResponse>

    @POST("api/v1/auth/password-recovery/reset")
    suspend fun passwordRecoveryReset(
        @Body body: PasswordRecoveryResetBody
    ): Response<PasswordRecoveryResetResponse>
}