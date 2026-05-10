package com.worksi.app.data.api

import com.worksi.app.data.model.CandidateRegisterResponseDto
import com.worksi.app.data.model.LoginRequest
import com.worksi.app.data.model.LoginResponse
import com.worksi.app.data.model.PasswordRecoveryRequestBody
import com.worksi.app.data.model.PasswordRecoveryRequestResponse
import com.worksi.app.data.model.PasswordRecoveryResetBody
import com.worksi.app.data.model.PasswordRecoveryResetResponse
import com.worksi.app.data.model.PasswordRecoveryVerifyBody
import com.worksi.app.data.model.PasswordRecoveryVerifyResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @Multipart
    @POST("api/v1/auth/register/candidate")
    suspend fun registerCandidate(
        @Part("data") data: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<CandidateRegisterResponseDto>

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