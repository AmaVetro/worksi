package com.worksi.app.data.model

import com.squareup.moshi.Json

data class CandidateCvJson(
    @Json(name = "cv_id") val cvId: Long,
    @Json(name = "original_filename") val originalFilename: String,
    @Json(name = "file_size_bytes") val fileSizeBytes: Int,
    @Json(name = "is_current") val isCurrent: Boolean,
    @Json(name = "uploaded_at") val uploadedAt: String? = null
)
