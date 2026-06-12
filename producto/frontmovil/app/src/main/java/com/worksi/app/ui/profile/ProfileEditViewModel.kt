package com.worksi.app.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.ApiErrorParser
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.CandidateProfilePatchJson
import com.worksi.app.data.model.CatalogItemDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ProfileEditUiState(
    val loading: Boolean = true,
    val saving: Boolean = false,
    val errorMessage: String? = null,
    val profileSummary: String = "",
    val salaryMin: Float = 1_000_000f,
    val salaryMax: Float = 2_500_000f,
    val yearsExperience: Int = 0,
    val regionId: Long? = null,
    val communeId: Long? = null,
    val sectorId: Long? = null,
    val selectedModalities: Set<String> = emptySet(),
    val selectedWorkloads: Set<String> = emptySet(),
    val selectedSkillIds: Set<Long> = emptySet(),
    val regions: List<CatalogItemDto> = emptyList(),
    val communes: List<CatalogItemDto> = emptyList(),
    val sectors: List<CatalogItemDto> = emptyList(),
    val skills: List<CatalogItemDto> = emptyList()
)

class ProfileEditViewModel(application: Application) : AndroidViewModel(application) {
    private val profileApi = RetrofitClient.candidateProfileApi
    private val catalogApi = RetrofitClient.catalogApi

    private val _ui = MutableStateFlow(ProfileEditUiState())
    val ui: StateFlow<ProfileEditUiState> = _ui.asStateFlow()

    init {
        reload()
    }

    fun reload() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, errorMessage = null)
            val err =
                withContext(Dispatchers.IO) {
                    try {
                        val profileResp = profileApi.getProfile()
                        if (!profileResp.isSuccessful) {
                            return@withContext ApiErrorParser.message(profileResp)
                        }
                        val profile = profileResp.body() ?: return@withContext "Respuesta vacía"
                        val regions = catalogApi.regions().body()?.items.orEmpty()
                        val sectors = catalogApi.sectors().body()?.items.orEmpty()
                        val communes =
                            if (profile.regionId > 0) {
                                catalogApi.communes(profile.regionId).body()?.items.orEmpty()
                            } else {
                                emptyList()
                            }
                        val sectorId = profile.sectorId
                        val skills =
                            if (sectorId != null && sectorId > 0) {
                                catalogApi.skills(sectorId).body()?.items.orEmpty()
                            } else {
                                emptyList()
                            }
                        val min = (profile.salaryExpectedMin ?: 1_000_000).toFloat()
                        val max = (profile.salaryExpectedMax ?: 2_500_000).toFloat()
                        _ui.value =
                            ProfileEditUiState(
                                loading = false,
                                profileSummary = profile.profileSummary.orEmpty(),
                                salaryMin = min,
                                salaryMax = max.coerceAtLeast(min),
                                yearsExperience = profile.yearsExperience,
                                regionId = profile.regionId,
                                communeId = profile.communeId,
                                sectorId = sectorId,
                                selectedModalities = profile.preferredModalities.toSet(),
                                selectedWorkloads = profile.preferredWorkloads.toSet(),
                                selectedSkillIds = profile.skills.map { it.id }.toSet(),
                                regions = regions,
                                communes = communes,
                                sectors = sectors,
                                skills = skills)
                        null
                    } catch (e: Exception) {
                        e.message ?: "Error de red"
                    }
                }
            if (err != null) {
                _ui.value = _ui.value.copy(loading = false, errorMessage = err)
            }
        }
    }

    fun setProfileSummary(value: String) {
        _ui.value = _ui.value.copy(profileSummary = value)
    }

    fun setSalaryRange(min: Float, max: Float) {
        _ui.value = _ui.value.copy(salaryMin = min, salaryMax = max)
    }

    fun setYearsExperience(value: Int) {
        _ui.value = _ui.value.copy(yearsExperience = value.coerceIn(0, 50))
    }

    fun setRegion(regionId: Long?) {
        _ui.value = _ui.value.copy(regionId = regionId, communeId = null, communes = emptyList())
        if (regionId == null) return
        viewModelScope.launch {
            val communes =
                withContext(Dispatchers.IO) {
                    catalogApi.communes(regionId).body()?.items.orEmpty()
                }
            _ui.value = _ui.value.copy(communes = communes)
        }
    }

    fun setCommune(communeId: Long?) {
        _ui.value = _ui.value.copy(communeId = communeId)
    }

    fun setSector(sectorId: Long?) {
        _ui.value = _ui.value.copy(sectorId = sectorId, skills = emptyList(), selectedSkillIds = emptySet())
        if (sectorId == null) return
        viewModelScope.launch {
            val skills =
                withContext(Dispatchers.IO) {
                    catalogApi.skills(sectorId).body()?.items.orEmpty()
                }
            _ui.value = _ui.value.copy(skills = skills)
        }
    }

    fun toggleModality(code: String) {
        val next = _ui.value.selectedModalities.toMutableSet()
        if (next.contains(code)) next.remove(code) else next.add(code)
        _ui.value = _ui.value.copy(selectedModalities = next)
    }

    fun toggleWorkload(code: String) {
        val next = _ui.value.selectedWorkloads.toMutableSet()
        if (next.contains(code)) next.remove(code) else next.add(code)
        _ui.value = _ui.value.copy(selectedWorkloads = next)
    }

    fun toggleSkill(skillId: Long) {
        val next = _ui.value.selectedSkillIds.toMutableSet()
        if (next.contains(skillId)) {
            next.remove(skillId)
        } else if (next.size < 12) {
            next.add(skillId)
        }
        _ui.value = _ui.value.copy(selectedSkillIds = next)
    }

    fun save(onSuccess: () -> Unit) {
        val s = _ui.value
        if (s.regionId == null || s.communeId == null || s.sectorId == null) {
            _ui.value = s.copy(errorMessage = "Seleccione región, comuna y rubro")
            return
        }
        if (s.selectedSkillIds.size !in 3..12) {
            _ui.value = s.copy(errorMessage = "Seleccione entre 3 y 12 skills")
            return
        }
        if (s.selectedModalities.isEmpty() || s.selectedWorkloads.isEmpty()) {
            _ui.value = s.copy(errorMessage = "Seleccione modalidades y cargas horarias")
            return
        }
        viewModelScope.launch {
            _ui.value = s.copy(saving = true, errorMessage = null)
            val err =
                withContext(Dispatchers.IO) {
                    try {
                        val body =
                            CandidateProfilePatchJson(
                                profileSummary = s.profileSummary.trim(),
                                salaryExpectedMin = s.salaryMin.toInt(),
                                salaryExpectedMax = s.salaryMax.toInt(),
                                yearsExperience = s.yearsExperience,
                                regionId = s.regionId,
                                communeId = s.communeId,
                                sectorId = s.sectorId,
                                preferredModalities = s.selectedModalities.toList(),
                                preferredWorkloads = s.selectedWorkloads.toList(),
                                skillsIds = s.selectedSkillIds.toList())
                        val resp = profileApi.patchProfile(body)
                        if (!resp.isSuccessful) {
                            ApiErrorParser.message(resp)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        e.message ?: "Error de red"
                    }
                }
            _ui.value = _ui.value.copy(saving = false, errorMessage = err)
            if (err == null) onSuccess()
        }
    }
}
