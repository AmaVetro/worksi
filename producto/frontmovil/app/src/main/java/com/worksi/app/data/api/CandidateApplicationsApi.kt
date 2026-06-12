package com.worksi.app.data.api

import com.worksi.app.data.model.CandidateApplicationDetailJson
import com.worksi.app.data.model.CandidateApplicationsPageJson
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CandidateApplicationsApi {
  @GET("api/v1/candidate/applications")
  suspend fun listApplications(
      @Query("page") page: Int,
      @Query("size") size: Int,
      @Query("sort") sort: String = "applied_at,desc"
  ): Response<CandidateApplicationsPageJson>

  @GET("api/v1/candidate/applications/{application_id}")
  suspend fun getApplication(
      @Path("application_id") applicationId: Long
  ): Response<CandidateApplicationDetailJson>

  @DELETE("api/v1/candidate/applications/{application_id}")
  suspend fun cancelApplication(
      @Path("application_id") applicationId: Long
  ): Response<Unit>
}
