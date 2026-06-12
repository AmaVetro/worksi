package com.worksi.app.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.ApiErrorParser
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.CandidateProfileJson
import com.worksi.app.data.model.CandidateProfilePatchJson
import com.worksi.app.data.model.CandidateProfileSkillJson
import com.worksi.app.data.model.CatalogItemDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

enum class ProfileEditSection {
    PERSONAL,
    DESCRIPTION,
    SALARY,
    YEARS,
    MODALITIES,
    WORKLOADS,
    SKILLS
}

data class ProfileUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val rawProfile: CandidateProfileJson? = null,
    val activeEditSection: ProfileEditSection? = null,
    val editSaving: Boolean = false,
    val editModalError: String? = null,
    val regions: List<CatalogItemDto> = emptyList(),
    val communes: List<CatalogItemDto> = emptyList(),
    val sectors: List<CatalogItemDto> = emptyList(),
    val skillsCatalog: List<CatalogItemDto> = emptyList(),
    val fullName: String = "",
    val sectorLine: String = "",
    val locationLine: String = "",
    val email: String = "",
    val phone: String = "",
    val description: String = "",
    val salaryLine: String = "",
    val yearsExperienceLine: String = "",
    val modalities: List<String> = emptyList(),
    val workloads: List<String> = emptyList(),
    val skills: List<CandidateProfileSkillJson> = emptyList(),
    val cvModalVisible: Boolean = false,
    val cvModalLoading: Boolean = false,
    val cvModalError: String? = null,
    val cvPdfBytes: ByteArray? = null,
    val cvFilename: String = "cv.pdf",
    val cvUploading: Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val profileApi = RetrofitClient.candidateProfileApi
    private val cvApi = RetrofitClient.candidateCvApi
    private val catalogApi = RetrofitClient.catalogApi

    private val _ui = MutableStateFlow(ProfileUiState())
    val ui: StateFlow<ProfileUiState> = _ui.asStateFlow()

    init {
        reload()
    }

    fun reload() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, errorMessage = null)
            val err =
                withContext(Dispatchers.IO) {
                    try {
                        val response = profileApi.getProfile()
                        if (!response.isSuccessful) {
                            ApiErrorParser.message(response)
                        } else {
                            val body = response.body()
                            if (body == null) {
                                "Respuesta vacía"
                            } else {
                                mapProfile(body)
                                null
                            }
                        }
                    } catch (e: Exception) {
                        e.message ?: "Error de red"
                    }
                }
            _ui.value = _ui.value.copy(isLoading = false, errorMessage = err)
        }
    }

    private suspend fun mapProfile(p: CandidateProfileJson) {
        val regionName = resolveName({ catalogApi.regions() }) { it.id == p.regionId }
        val communeName = resolveName({ catalogApi.communes(p.regionId) }) { it.id == p.communeId }
        val sectorName =
            p.sectorId?.let { sid -> resolveName({ catalogApi.sectors() }) { it.id == sid } }.orEmpty()
        val location =
            when {
                regionName.isNotBlank() && communeName.isNotBlank() -> "$regionName · $communeName"
                regionName.isNotBlank() -> regionName
                communeName.isNotBlank() -> communeName
                else -> "—"
            }
        val middle = p.middleName?.trim().orEmpty()
        val maternal = p.lastNameMaternal?.trim().orEmpty()
        val fullName = buildString {
            append(p.firstName.trim())
            if (middle.isNotEmpty()) {
                append(' ')
                append(middle)
            }
            append(' ')
            append(p.lastNamePaternal.trim())
            if (maternal.isNotEmpty()) {
                append(' ')
                append(maternal)
            }
        }
        _ui.value =
            _ui.value.copy(
                isLoading = false,
                errorMessage = null,
                rawProfile = p,
                fullName = fullName.trim(),
                sectorLine = if (sectorName.isBlank()) "—" else sectorName,
                locationLine = location,
                email = p.email.trim(),
                phone = p.phone?.trim().orEmpty().ifBlank { "—" },
                description = p.profileSummary?.trim().orEmpty().ifBlank { "Sin descripción personal." },
                salaryLine = formatSalaryRange(p.salaryExpectedMin, p.salaryExpectedMax),
                yearsExperienceLine = formatYearsExperience(p.yearsExperience),
                modalities = p.preferredModalities.map { modalityLabel(it) },
                workloads = p.preferredWorkloads.map { workloadLabel(it) },
                skills = p.skills,
                activeEditSection = null,
                editSaving = false,
                editModalError = null)
    }

    fun openEditSection(section: ProfileEditSection) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(activeEditSection = section, editModalError = null)
            when (section) {
                ProfileEditSection.PERSONAL -> ensurePersonalCatalogs()
                ProfileEditSection.SKILLS -> ensureSkillsCatalog()
                else -> Unit
            }
        }
    }

    fun closeEditSection() {
        _ui.value = _ui.value.copy(activeEditSection = null, editModalError = null)
    }

    fun onEditRegionSelected(regionId: Long) {
        viewModelScope.launch {
            val communes =
                withContext(Dispatchers.IO) {
                    catalogApi.communes(regionId).body()?.items.orEmpty()
                }
            _ui.value = _ui.value.copy(communes = communes)
        }
    }

    fun saveProfilePatch(patch: CandidateProfilePatchJson) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(editSaving = true, editModalError = null)
            val err =
                withContext(Dispatchers.IO) {
                    try {
                        val resp = profileApi.patchProfile(patch)
                        if (!resp.isSuccessful) {
                            ApiErrorParser.message(resp)
                        } else {
                            val body = resp.body()
                            if (body == null) {
                                "Respuesta vacía"
                            } else {
                                mapProfile(body)
                                null
                            }
                        }
                    } catch (e: Exception) {
                        e.message ?: "Error de red"
                    }
                }
            if (err != null) {
                _ui.value = _ui.value.copy(editSaving = false, editModalError = err)
            }
        }
    }

    private suspend fun ensurePersonalCatalogs() {
        val regions =
            withContext(Dispatchers.IO) { catalogApi.regions().body()?.items.orEmpty() }
        val sectors =
            withContext(Dispatchers.IO) { catalogApi.sectors().body()?.items.orEmpty() }
        val regionId = _ui.value.rawProfile?.regionId
        val communes =
            if (regionId != null) {
                withContext(Dispatchers.IO) {
                    catalogApi.communes(regionId).body()?.items.orEmpty()
                }
            } else {
                emptyList()
            }
        _ui.value = _ui.value.copy(regions = regions, sectors = sectors, communes = communes)
    }

    private suspend fun ensureSkillsCatalog() {
        val sectorId = _ui.value.rawProfile?.sectorId ?: return
        val skills =
            withContext(Dispatchers.IO) {
                catalogApi.skills(sectorId).body()?.items.orEmpty()
            }
        _ui.value = _ui.value.copy(skillsCatalog = skills)
    }

    private suspend fun resolveName(
        fetch: suspend () -> retrofit2.Response<com.worksi.app.data.model.CatalogListDto>,
        match: (CatalogItemDto) -> Boolean
    ): String {
        val response = fetch()
        if (!response.isSuccessful) return ""
        return response.body()?.items?.firstOrNull(match)?.name?.trim().orEmpty()
    }

    private fun formatSalaryRange(min: Int?, max: Int?): String {
        val fmt = NumberFormat.getNumberInstance(Locale("es", "CL"))
        val minStr = min?.let { "Mín: $${fmt.format(it.toLong())}" }
        val maxStr = max?.let { "Máx: $${fmt.format(it.toLong())}" }
        return when {
            minStr != null && maxStr != null -> "$minStr  $maxStr"
            minStr != null -> minStr
            maxStr != null -> maxStr
            else -> "—"
        }
    }

    private fun formatYearsExperience(years: Int): String {
        return if (years <= 0) {
            "Sin experiencia laboral declarada"
        } else if (years == 1) {
            "1 año de experiencia"
        } else {
            "$years años de experiencia"
        }
    }

    private fun modalityLabel(code: String): String =
        when (code.uppercase(Locale.ROOT)) {
            "REMOTE" -> "Remoto"
            "HYBRID" -> "Híbrido"
            "ONSITE" -> "Presencial"
            else -> code
        }

    private fun workloadLabel(code: String): String =
        when (code.uppercase(Locale.ROOT)) {
            "FULL_TIME" -> "Full Time"
            "PART_TIME" -> "Part Time"
            "OTHER" -> "Otro"
            else -> code
        }

    fun openCvModal() {
        viewModelScope.launch {
            _ui.value =
                _ui.value.copy(
                    cvModalVisible = true,
                    cvModalLoading = true,
                    cvModalError = null,
                    cvPdfBytes = null)
            val err =
                withContext(Dispatchers.IO) {
                    try {
                        val metaResp = cvApi.getCurrentCv()
                        if (!metaResp.isSuccessful) {
                            return@withContext ApiErrorParser.message(metaResp)
                        }
                        val meta = metaResp.body() ?: return@withContext "Respuesta vacía"
                        val fileResp = cvApi.getCurrentCvFile()
                        if (!fileResp.isSuccessful) {
                            return@withContext ApiErrorParser.message(fileResp)
                        }
                        val bytes = fileResp.body()?.bytes()
                        if (bytes == null || bytes.isEmpty()) {
                            return@withContext "CV vacío"
                        }
                        _ui.value =
                            _ui.value.copy(
                                cvFilename = meta.originalFilename.ifBlank { "cv.pdf" },
                                cvPdfBytes = bytes)
                        null
                    } catch (e: Exception) {
                        e.message ?: "Error de red"
                    }
                }
            _ui.value = _ui.value.copy(cvModalLoading = false, cvModalError = err)
        }
    }

    fun closeCvModal() {
        _ui.value =
            _ui.value.copy(
                cvModalVisible = false,
                cvModalLoading = false,
                cvModalError = null,
                cvPdfBytes = null)
    }

    fun setCvModalError(message: String?) {
        _ui.value = _ui.value.copy(cvModalError = message)
    }

    fun uploadCv(pdfBytes: ByteArray, filename: String) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(cvUploading = true, cvModalError = null)
            val err =
                withContext(Dispatchers.IO) {
                    try {
                        val safeName =
                            filename.takeIf { it.lowercase(Locale.ROOT).endsWith(".pdf") }
                                ?: "cv.pdf"
                        val part =
                            MultipartBody.Part.createFormData(
                                "file",
                                safeName,
                                pdfBytes.toRequestBody("application/pdf".toMediaType()))
                        val resp = cvApi.uploadCv(part)
                        if (!resp.isSuccessful) {
                            ApiErrorParser.message(resp)
                        } else {
                            val body = resp.body()
                            if (body == null) {
                                "Respuesta vacía"
                            } else {
                                _ui.value =
                                    _ui.value.copy(
                                        cvFilename = body.originalFilename.ifBlank { safeName },
                                        cvPdfBytes = pdfBytes,
                                        cvModalLoading = false)
                                null
                            }
                        }
                    } catch (e: Exception) {
                        e.message ?: "Error de red"
                    }
                }
            _ui.value = _ui.value.copy(cvUploading = false, cvModalError = err)
        }
    }
}
