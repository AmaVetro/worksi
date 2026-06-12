package com.worksi.app.data.api

import com.worksi.app.data.model.CandidateProfileJson
import com.worksi.app.data.model.CandidateProfilePatchJson
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface CandidateProfileApi {
    @GET("api/v1/candidate/profile")
    suspend fun getProfile(): Response<CandidateProfileJson>

    @PATCH("api/v1/candidate/profile")
    suspend fun patchProfile(@Body body: CandidateProfilePatchJson): Response<CandidateProfileJson>
}
