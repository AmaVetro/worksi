package com.worksi.app.data.model

import com.squareup.moshi.Json

data class CandidateFeedPageJson(
    val items: List<CandidateJobFeedItemJson>,
    val page: Int,
    val size: Int,
    @Json(name = "total_items") val totalItems: Long,
    @Json(name = "total_pages") val totalPages: Int
)

data class CandidateJobFeedItemJson(
    @Json(name = "job_id") val jobId: Long,
    val title: String,
    @Json(name = "company_name") val companyName: String,
    @Json(name = "salary_offered") val salaryOffered: Int,
    @Json(name = "commune_name") val communeName: String,
    val modality: String,
    @Json(name = "years_experience_required") val yearsExperienceRequired: Int,
    @Json(name = "description_preview") val descriptionPreview: String,
    @Json(name = "skills_preview") val skillsPreview: List<CandidateSkillPreviewJson>,
    @Json(name = "external_image_url") val externalImageUrl: String? = null,
    @Json(name = "has_protected_job_image") val hasProtectedJobImage: Boolean = false,
    val match: CandidateJobMatchJson?
)

data class CandidateSkillPreviewJson(val id: Long, val name: String)

data class CandidateJobMatchJson(
    val score: Double?,
    @Json(name = "explanation_short") val explanationShort: String?
)

data class CandidateSwipeBody(@Json(name = "job_id") val jobId: Long, val action: String)

data class CandidateJobDetailJson(
    @Json(name = "job_id") val jobId: Long,
    val title: String,
    @Json(name = "company_name") val companyName: String,
    @Json(name = "salary_offered") val salaryOffered: Int,
    @Json(name = "commune_name") val communeName: String,
    val modality: String,
    @Json(name = "years_experience_required") val yearsExperienceRequired: Int,
    val description: String,
    val workload: String,
    val skills: List<CandidateSkillPreviewJson>,
    @Json(name = "external_image_url") val externalImageUrl: String? = null,
    @Json(name = "has_protected_job_image") val hasProtectedJobImage: Boolean = false,
    val match: CandidateJobDetailMatchJson?
)

data class CandidateJobDetailMatchJson(
    val score: Double?,
    val explanation: String?
)
