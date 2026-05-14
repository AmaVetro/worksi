package com.worksi.app.ui.register

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.local.SecureTokenStore
import com.worksi.app.data.model.CandidateRegisterPayload
import com.worksi.app.data.model.CatalogItemDto
import com.worksi.app.validation.PdfCvTextRules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

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
    val catalogError: String? = null,
    val registerSubmitting: Boolean = false,
    val registerError: String? = null
)

class CandidateRegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val catalogApi = RetrofitClient.catalogApi
    private val authService = RetrofitClient.authService
    private val registerPayloadAdapter =
        RetrofitClient.moshi.adapter(CandidateRegisterPayload::class.java)

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

    suspend fun validateCvPdfSelectable(uriString: String): String? {
        return withContext(Dispatchers.IO) {
            val app = getApplication<Application>()
            val uri = Uri.parse(uriString)
            val bytes =
                app.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: return@withContext "No se pudo leer el archivo"
            if (bytes.size > 1_048_576) {
                return@withContext "El PDF supera 1 MB"
            }
            if (bytes.size < 4 ||
                bytes[0] != '%'.code.toByte() ||
                bytes[1] != 'P'.code.toByte() ||
                bytes[2] != 'D'.code.toByte() ||
                bytes[3] != 'F'.code.toByte()
            ) {
                return@withContext "El archivo debe ser un PDF válido"
            }
            PdfCvTextRules.validatePdfBytes(bytes)
        }
    }

    fun clearCatalogError() {
        _ui.update { it.copy(catalogError = null) }
    }

    fun clearRegisterError() {
        _ui.update { it.copy(registerError = null) }
    }

    fun submitRegistration(onSuccess: () -> Unit) {
        val d = _ui.value.draft
        if (!d.consentAccepted) {
            _ui.update { it.copy(registerError = "Debes aceptar el uso de datos") }
            return
        }
        if (d.regionId == null || d.communeId == null || d.sectorId == null) {
            _ui.update { it.copy(registerError = "Faltan region, comuna o rubro") }
            return
        }
        if (d.skillIds.size < 3 || d.skillIds.size > 12) {
            _ui.update { it.copy(registerError = "Selecciona entre 3 y 12 skills") }
            return
        }
        if (d.modalities.isEmpty() || d.workloads.isEmpty()) {
            _ui.update { it.copy(registerError = "Indica modalidades y cargas horarias") }
            return
        }
        if (d.cvUri.isNullOrBlank()) {
            _ui.update { it.copy(registerError = "Debes adjuntar un CV en PDF") }
            return
        }
        viewModelScope.launch {
            _ui.update { it.copy(registerSubmitting = true, registerError = null) }
            val outcome =
                withContext(Dispatchers.IO) {
                    runCatching {
                        val app = getApplication<Application>()
                        val uri = Uri.parse(d.cvUri)
                        val pdfBytes =
                            app.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                                ?: throw IllegalStateException("No se pudo leer el CV")
                        if (pdfBytes.isEmpty()) {
                            throw IllegalStateException("CV vacio")
                        }
                        if (pdfBytes.size > 1_048_576) {
                            throw IllegalStateException("El CV supera 1 MB")
                        }
                        if (pdfBytes.size < 4 ||
                            pdfBytes[0] != '%'.code.toByte() ||
                            pdfBytes[1] != 'P'.code.toByte() ||
                            pdfBytes[2] != 'D'.code.toByte() ||
                            pdfBytes[3] != 'F'.code.toByte()
                        ) {
                            throw IllegalStateException("El archivo debe ser PDF")
                        }
                        val smin = d.salaryMin.toIntOrNull()
                        val smax = d.salaryMax.toIntOrNull()
                        val salaryMinOut =
                            if (smin != null && smax != null && smin <= smax) smin else null
                        val salaryMaxOut =
                            if (salaryMinOut != null) smax else null
                        val mid = d.middleName.trim().ifEmpty { null }
                        val street = d.street.trim().ifEmpty { null }
                        val summary = d.profileSummary.trim().ifEmpty { null }
                        val payload =
                            CandidateRegisterPayload(
                                email = d.email.trim(),
                                password = d.password,
                                firstName = d.firstName.trim(),
                                middleName = mid,
                                lastNamePaternal = d.lastNamePaternal.trim(),
                                lastNameMaternal = d.lastNameMaternal.trim(),
                                phone = d.phone.trim(),
                                rut = d.rut.trim(),
                                documentNumber = d.documentNumber.trim(),
                                street = street,
                                regionId = d.regionId!!,
                                communeId = d.communeId!!,
                                consentGiven = true,
                                sectorId = d.sectorId!!,
                                profileSummary = summary,
                                salaryExpectedMin = salaryMinOut,
                                salaryExpectedMax = salaryMaxOut,
                                preferredModalities = d.modalities.toList(),
                                preferredWorkloads = d.workloads.toList(),
                                skillsIds = d.skillIds.toList()
                            )
                        val json = registerPayloadAdapter.toJson(payload)
                        val dataBody =
                            json.toRequestBody("application/json; charset=UTF-8".toMediaType())
                        val fileName =
                            d.cvDisplayName?.takeIf { it.lowercase().endsWith(".pdf") } ?: "cv.pdf"
                        val filePart =
                            MultipartBody.Part.createFormData(
                                "file",
                                fileName,
                                pdfBytes.toRequestBody("application/pdf".toMediaType())
                            )
                        val resp = authService.registerCandidate(dataBody, filePart)
                        if (resp.isSuccessful && resp.body() != null) {
                            SecureTokenStore.saveAccessToken(resp.body()!!.accessToken)
                            Result.success(Unit)
                        } else {
                            val raw = resp.errorBody()?.string().orEmpty()
                            val hint =
                                if (raw.contains("\"message\"")) {
                                    try {
                                        val start = raw.indexOf("\"message\"")
                                        val sub = raw.substring(start)
                                        val colon = sub.indexOf(':')
                                        val rest = sub.substring(colon + 1).trim()
                                        if (rest.startsWith("\"")) {
                                            val end = rest.indexOf('"', 1)
                                            if (end > 1) rest.substring(1, end) else raw
                                        } else raw
                                    } catch (_: Exception) {
                                        raw
                                    }
                                } else {
                                    "Error ${resp.code()}"
                                }
                            throw IllegalStateException(hint)
                        }
                    }
                }
            outcome.fold(
                onSuccess = {
                    _ui.update { it.copy(registerSubmitting = false, registerError = null) }
                    onSuccess()
                },
                onFailure = { e ->
                    _ui.update {
                        it.copy(
                            registerSubmitting = false,
                            registerError = e.message ?: "Error al registrar"
                        )
                    }
                }
            )
        }
    }
}
