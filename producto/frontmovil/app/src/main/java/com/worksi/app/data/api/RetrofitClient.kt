package com.worksi.app.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://backend-production-f9dc.up.railway.app/"

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)

    val catalogApi: CatalogApi = retrofit.create(CatalogApi::class.java)

    val candidateJobsApi: CandidateJobsApi = retrofit.create(CandidateJobsApi::class.java)

    val candidateProfileApi: CandidateProfileApi = retrofit.create(CandidateProfileApi::class.java)

    val candidateApplicationsApi: CandidateApplicationsApi =
        retrofit.create(CandidateApplicationsApi::class.java)

    val candidateCvApi: CandidateCvApi = retrofit.create(CandidateCvApi::class.java)

    val messagingApi: MessagingApi = retrofit.create(MessagingApi::class.java)

    fun candidateJobImageUrl(jobId: Long): String {
        val base = BASE_URL.trimEnd('/')
        return "$base/api/v1/candidate/jobs/$jobId/image"
    }
}