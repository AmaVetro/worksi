package com.worksi.app.data.api

import com.worksi.app.data.model.CandidateCvJson
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Streaming

interface CandidateCvApi {
    @GET("api/v1/candidate/cv/current")
    suspend fun getCurrentCv(): Response<CandidateCvJson>

    @GET("api/v1/candidate/cv/current/file")
    @Streaming
    suspend fun getCurrentCvFile(): Response<ResponseBody>

    @Multipart
    @POST("api/v1/candidate/cv")
    suspend fun uploadCv(@Part file: MultipartBody.Part): Response<CandidateCvJson>
}
