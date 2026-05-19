package com.worksi.app.data.model

import com.squareup.moshi.Json

data class CandidateProfileSkillJson(
    val id: Long,
    val name: String
)

data class CandidateProfileJson(
    @Json(name = "first_name") val firstName: String,
    @Json(name = "middle_name") val middleName: String?,
    @Json(name = "last_name_paternal") val lastNamePaternal: String,
    @Json(name = "last_name_maternal") val lastNameMaternal: String?,
    val phone: String?,
    val email: String,
    @Json(name = "region_id") val regionId: Long,
    @Json(name = "commune_id") val communeId: Long,
    @Json(name = "sector_id") val sectorId: Long?,
    @Json(name = "profile_summary") val profileSummary: String?,
    @Json(name = "salary_expected_min") val salaryExpectedMin: Int?,
    @Json(name = "salary_expected_max") val salaryExpectedMax: Int?,
    @Json(name = "preferred_modalities") val preferredModalities: List<String> = emptyList(),
    @Json(name = "preferred_workloads") val preferredWorkloads: List<String> = emptyList(),
    val skills: List<CandidateProfileSkillJson> = emptyList()
)
