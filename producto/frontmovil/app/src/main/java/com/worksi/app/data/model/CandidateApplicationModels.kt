package com.worksi.app.data.model

import com.squareup.moshi.Json

data class MatchBreakdownJson(
    @Json(name = "final_score") val finalScore: Double?,
    @Json(name = "description_score") val descriptionScore: Double?,
    @Json(name = "title_score") val titleScore: Double?,
    @Json(name = "modality_score") val modalityScore: Double?,
    @Json(name = "workload_score") val workloadScore: Double?,
    @Json(name = "experience_score") val experienceScore: Double?
)

data class CandidateApplicationsPageJson(
    val items: List<CandidateApplicationListItemJson>,
    val page: Int,
    val size: Int,
    @Json(name = "total_items") val totalItems: Long,
    @Json(name = "total_pages") val totalPages: Int
)

data class CandidateApplicationListItemJson(
    @Json(name = "application_id") val applicationId: Long,
    @Json(name = "job_id") val jobId: Long,
    @Json(name = "job_title") val jobTitle: String,
    @Json(name = "company_commercial_name") val companyCommercialName: String,
    @Json(name = "salary_offered") val salaryOffered: Int,
    val status: String,
    @Json(name = "applied_at") val appliedAt: String?,
    @Json(name = "match_score") val matchScore: Double?
)

data class CandidateApplicationDetailJson(
    @Json(name = "application_id") val applicationId: Long,
    @Json(name = "job_id") val jobId: Long,
    val status: String,
    @Json(name = "applied_at") val appliedAt: String?,
    @Json(name = "viewed_at") val viewedAt: String?,
    @Json(name = "job_title") val jobTitle: String,
    @Json(name = "company_commercial_name") val companyCommercialName: String,
    @Json(name = "salary_offered") val salaryOffered: Int,
    val modality: String,
    @Json(name = "years_experience_required") val yearsExperienceRequired: Int,
    @Json(name = "commune_name") val communeName: String,
    val description: String,
    @Json(name = "match_score") val matchScore: Double?
)
