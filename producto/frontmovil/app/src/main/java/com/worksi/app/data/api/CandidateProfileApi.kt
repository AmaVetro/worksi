package com.worksi.app.data.api

import com.worksi.app.data.model.CandidateProfileJson
import retrofit2.Response
import retrofit2.http.GET

interface CandidateProfileApi {
    @GET("api/v1/candidate/profile")
    suspend fun getProfile(): Response<CandidateProfileJson>
}
