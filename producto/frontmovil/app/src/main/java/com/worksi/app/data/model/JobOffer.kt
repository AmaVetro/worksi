package com.worksi.app.data.model

data class JobOffer(
    val id: Long,
    val title: String,
    val company: String,
    val location: String,
    val modality: String,       // "Remoto", "Presencial", "Híbrido"
    val salary: Int,
    val experienceYears: Int,
    val description: String,
    val skills: List<String>,
    val matchPercentage: Float   // 0..100
)