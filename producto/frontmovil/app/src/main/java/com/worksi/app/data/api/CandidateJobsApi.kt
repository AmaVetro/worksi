package com.worksi.app.data.api

import com.worksi.app.data.model.CandidateApplicationBody
import com.worksi.app.data.model.CandidateApplicationResponse
import com.worksi.app.data.model.CandidateFeedPageJson
import com.worksi.app.data.model.CandidateJobDetailJson
import com.worksi.app.data.model.CandidateSwipeBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CandidateJobsApi {
  @GET("api/v1/candidate/jobs/feed")
  suspend fun getFeed(
      @Query("page") page: Int,
      @Query("size") size: Int
  ): Response<CandidateFeedPageJson>

  @GET("api/v1/candidate/jobs/{job_id}")
  suspend fun getJobDetail(@Path("job_id") jobId: Long): Response<CandidateJobDetailJson>

  @POST("api/v1/candidate/swipes")
  suspend fun postSwipe(@Body body: CandidateSwipeBody): Response<Unit>

    @POST("api/v1/candidate/applications")
    suspend fun postApplication(@Body body: CandidateApplicationBody): Response<CandidateApplicationResponse>

}

