package com.worksi.app.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.CatalogItemDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterDraft(
    val firstName: String = "",
    val middleName: String = "",
    val lastNamePaternal: String = "",
    val lastNameMaternal: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val rut: String = "",
    val documentNumber: String = "",
    val street: String = "",
    val regionId: Long? = null,
    val communeId: Long? = null,
    val sectorId: Long? = null,
    val skillIds: Set<Long> = emptySet(),
    val profileSummary: String = "",
    val salaryMin: String = "",
    val salaryMax: String = "",
    val modalities: Set<String> = emptySet(),
    val workloads: Set<String> = emptySet(),
    val cvUri: String? = null,
    val cvDisplayName: String? = null,
    val consentAccepted: Boolean = false
)

data class CandidateRegisterUiState(
    val draft: RegisterDraft = RegisterDraft(),
    val regions: List<CatalogItemDto> = emptyList(),
    val communes: List<CatalogItemDto> = emptyList(),
    val sectors: List<CatalogItemDto> = emptyList(),
    val skillsForSector: List<CatalogItemDto> = emptyList(),
    val catalogLoading: Boolean = false,
    val catalogError: String? = null
)

class CandidateRegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val catalogApi = RetrofitClient.catalogApi

    private val _ui = MutableStateFlow(CandidateRegisterUiState())
    val ui: StateFlow<CandidateRegisterUiState> = _ui.asStateFlow()

    fun reset() {
        _ui.value = CandidateRegisterUiState()
    }

    fun updateDraft(transform: (RegisterDraft) -> RegisterDraft) {
        _ui.update { it.copy(draft = transform(it.draft)) }
    }

    fun loadRegions() {
        viewModelScope.launch {
            _ui.update { it.copy(catalogLoading = true, catalogError = null) }
            try {
                val resp = catalogApi.regions()
                if (resp.isSuccessful && resp.body() != null) {
                    _ui.update {
                        it.copy(
                            regions = resp.body()!!.items,
                            catalogLoading = false
                        )
                    }
                } else {
                    _ui.update {
                        it.copy(
                            catalogLoading = false,
                            catalogError = "No se pudieron cargar regiones"
                        )
                    }
                }
            } catch (_: Exception) {
                _ui.update {
                    it.copy(
                        catalogLoading = false,
                        catalogError = "Error de conexion"
                    )
                }
            }
        }
    }

    fun loadCommunes(regionId: Long) {
        viewModelScope.launch {
            _ui.update { it.copy(catalogLoading = true, catalogError = null) }
            try {
                val resp = catalogApi.communes(regionId)
                if (resp.isSuccessful && resp.body() != null) {
                    _ui.update {
                        it.copy(
                            communes = resp.body()!!.items,
                            catalogLoading = false
                        )
                    }
                } else {
                    _ui.update {
                        it.copy(
                            communes = emptyList(),
                            catalogLoading = false,
                            catalogError = "No se pudieron cargar comunas"
                        )
                    }
                }
            } catch (_: Exception) {
                _ui.update {
                    it.copy(
                        catalogLoading = false,
                        catalogError = "Error de conexion"
                    )
                }
            }
        }
    }

    fun loadSectors() {
        viewModelScope.launch {
            _ui.update { it.copy(catalogLoading = true, catalogError = null) }
            try {
                val resp = catalogApi.sectors()
                if (resp.isSuccessful && resp.body() != null) {
                    _ui.update {
                        it.copy(
                            sectors = resp.body()!!.items,
                            catalogLoading = false
                        )
                    }
                } else {
                    _ui.update {
                        it.copy(
                            catalogLoading = false,
                            catalogError = "No se pudieron cargar rubros"
                        )
                    }
                }
            } catch (_: Exception) {
                _ui.update {
                    it.copy(
                        catalogLoading = false,
                        catalogError = "Error de conexion"
                    )
                }
            }
        }
    }

    fun loadSkillsForSector(sectorId: Long) {
        viewModelScope.launch {
            _ui.update { it.copy(catalogLoading = true, catalogError = null) }
            try {
                val resp = catalogApi.skills(sectorId)
                if (resp.isSuccessful && resp.body() != null) {
                    _ui.update {
                        it.copy(
                            skillsForSector = resp.body()!!.items,
                            catalogLoading = false
                        )
                    }
                } else {
                    _ui.update {
                        it.copy(
                            skillsForSector = emptyList(),
                            catalogLoading = false,
                            catalogError = "Rubro invalido o sin skills"
                        )
                    }
                }
            } catch (_: Exception) {
                _ui.update {
                    it.copy(
                        catalogLoading = false,
                        catalogError = "Error de conexion"
                    )
                }
            }
        }
    }

    fun onRegionSelected(regionId: Long) {
        updateDraft {
            it.copy(
                regionId = regionId,
                communeId = null
            )
        }
        loadCommunes(regionId)
    }

    fun onSectorSelected(sectorId: Long) {
        updateDraft {
            it.copy(
                sectorId = sectorId,
                skillIds = emptySet()
            )
        }
        loadSkillsForSector(sectorId)
    }

    fun toggleSkill(skillId: Long) {
        updateDraft { d ->
            val next = d.skillIds.toMutableSet()
            if (next.contains(skillId)) {
                next.remove(skillId)
            } else if (next.size < 12) {
                next.add(skillId)
            }
            d.copy(skillIds = next)
        }
    }

    fun toggleModality(code: String) {
        updateDraft { d ->
            val next = d.modalities.toMutableSet()
            if (next.contains(code)) next.remove(code) else next.add(code)
            d.copy(modalities = next)
        }
    }

    fun toggleWorkload(code: String) {
        updateDraft { d ->
            val next = d.workloads.toMutableSet()
            if (next.contains(code)) next.remove(code) else next.add(code)
            d.copy(workloads = next)
        }
    }

    fun setCv(uri: String, displayName: String?) {
        updateDraft { it.copy(cvUri = uri, cvDisplayName = displayName) }
    }

    fun clearCv() {
        updateDraft { it.copy(cvUri = null, cvDisplayName = null) }
    }

    fun clearCatalogError() {
        _ui.update { it.copy(catalogError = null) }
    }
}
