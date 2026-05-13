package com.worksi.app.ui.home

import androidx.lifecycle.ViewModel
import com.worksi.app.data.model.JobOffer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _offer = MutableStateFlow(
        JobOffer(
            id = 1,
            title = "Desarrollador Backend",
            company = "Google",
            communeName = "Providencia",
            modality = "Remoto",
            salary = 750000,
            experienceYears = 2,
            description = "Buscamos desarrollador backend con experiencia en APIs REST, Spring Boot y bases de datos relacionales. Se valorará experiencia en seguridad (JWT), testing automatizado e integración continua. Capacidad para diseñar arquitecturas escalables y...",
            skills = listOf("Java", "Spring", "SQL", "Postman"),
            matchPercentage = 85f
        )
    )
    val offer: StateFlow<JobOffer> = _offer.asStateFlow()

    // Acciones mock (no hacen nada aún)
    fun onPostular() { /* TODO: conectar con endpoint de postulación */ }
    fun onGuardar() { /* TODO: guardar oferta */ }
    fun onPasar() { /* TODO: pasar a siguiente oferta */ }
}