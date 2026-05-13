package com.worksi.app.data.model

data class JobOffer(
    val id: Long,
    val title: String,
    val company: String,
    val communeName: String,
    val modality: String,
    val salary: Int,
    val experienceYears: Int,
    val description: String,
    val skills: List<String>,
    val matchPercentage: Float
)