package com.worksi.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CandidateRegisterResponseDto(
    @Json(name = "user_id") val userId: Long,
    @Json(name = "role") val role: String,
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String,
    @Json(name = "expires_in") val expiresIn: Int
)

@JsonClass(generateAdapter = true)
data class CandidateRegisterPayload(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "middle_name") val middleName: String?,
    @Json(name = "last_name_paternal") val lastNamePaternal: String,
    @Json(name = "last_name_maternal") val lastNameMaternal: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "rut") val rut: String,
    @Json(name = "document_number") val documentNumber: String,
    @Json(name = "street") val street: String?,
    @Json(name = "region_id") val regionId: Long,
    @Json(name = "commune_id") val communeId: Long,
    @Json(name = "consent_given") val consentGiven: Boolean,
    @Json(name = "sector_id") val sectorId: Long,
    @Json(name = "profile_summary") val profileSummary: String?,
    @Json(name = "salary_expected_min") val salaryExpectedMin: Int?,
    @Json(name = "salary_expected_max") val salaryExpectedMax: Int?,
    @Json(name = "years_experience") val yearsExperience: Int,
    @Json(name = "preferred_modalities") val preferredModalities: List<String>,
    @Json(name = "preferred_workloads") val preferredWorkloads: List<String>,
    @Json(name = "skills_ids") val skillsIds: List<Long>
)
