package com.worksi.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.CandidateJobsApi
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.local.SecureTokenStore
import com.worksi.app.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class HomeUiState(
  val currentOffer: JobOffer? = null,
  val isLoading: Boolean = false,
  val actionBusy: Boolean = false,
  val empty: Boolean = false,
  val errorMessage: String? = null,
  val pendingOffers: List<JobOffer> = emptyList()
)

class HomeViewModel : ViewModel() {
  private val jobsApi: CandidateJobsApi = RetrofitClient.candidateJobsApi
  private val tokenStore = SecureTokenStore

  private val _uiState = MutableStateFlow(HomeUiState())
  val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

  // Exponemos StateFlow individuales para mantener compatibilidad con la pantalla actual
  val offer: StateFlow<JobOffer?> = _uiState.map { it.currentOffer }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = null
  )

  val isLoading: StateFlow<Boolean> = _uiState.map { it.isLoading }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = false
  )

  val empty: StateFlow<Boolean> = _uiState.map { it.empty }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = false
  )

  val errorMessage: StateFlow<String?> = _uiState.map { it.errorMessage }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = null
  )

  val actionBusy: StateFlow<Boolean> = _uiState.map { it.actionBusy }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = false
  )

  private var currentPage = 1
  private var totalPages = 1
  private var isLoadingMore = false

  init {
    loadFirstPage()
  }

  fun loadFirstPage() {
    if (_uiState.value.isLoading) return
    currentPage = 1
    loadFeed(reset = true)
  }

  fun retry() {
    loadFirstPage()
  }

  private fun loadFeed(reset: Boolean = false) {
    if (isLoadingMore) return
    viewModelScope.launch {
      try {
        _uiState.update { it.copy(isLoading = reset, errorMessage = null) }
        isLoadingMore = true

        val token = tokenStore.getAccessToken()
        if (token.isNullOrBlank()) {
          _uiState.update {
            it.copy(isLoading = false, errorMessage = "Sesión no válida, inicia sesión de nuevo")
          }
          return@launch
        }

        val response = jobsApi.getFeed(page = currentPage, size = 10)
        if (response.isSuccessful) {
          val body = response.body()
          if (body != null && body.items.isNotEmpty()) {
            totalPages = body.totalPages
            val newOffers = body.items.map { it.toJobOffer() }
            if (reset) {
              _uiState.update {
                it.copy(
                  pendingOffers = newOffers.drop(1),
                  currentOffer = newOffers.firstOrNull(),
                  isLoading = false,
                  empty = false
                )
              }
            } else {
              _uiState.update {
                it.copy(
                  pendingOffers = it.pendingOffers + newOffers,
                  isLoading = false
                )
              }
            }
            currentPage++
          } else {
            if (reset) {
              _uiState.update {
                it.copy(isLoading = false, empty = true, currentOffer = null)
              }
            } else {
              _uiState.update { it.copy(isLoading = false) }
            }
          }
        } else {
          val errorMsg = when (response.code()) {
            401 -> "Sesión expirada, inicia sesión nuevamente"
            404 -> "No se encontraron ofertas"
            else -> "Error al cargar ofertas (${response.code()})"
          }
          _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
        }
      } catch (e: IOException) {
        _uiState.update { it.copy(isLoading = false, errorMessage = "Error de red: ${e.message}") }
      } catch (e: HttpException) {
        _uiState.update { it.copy(isLoading = false, errorMessage = "Error de servidor: ${e.code()}") }
      } finally {
        isLoadingMore = false
      }
    }
  }

  fun onPasar() {
    val current = _uiState.value.currentOffer ?: return
    viewModelScope.launch {
      _uiState.update { it.copy(actionBusy = true) }
      try {
        // 🔁 Modo prueba: no enviar swipe al backend
        // val response = jobsApi.postSwipe(CandidateSwipeBody(current.id, "PASS"))
        // if (response.isSuccessful) {
        moveToNextOffer()
        // } else {
        //     _uiState.update { it.copy(actionBusy = false, errorMessage = "Error al descartar") }
        // }
      } catch (e: Exception) {
        _uiState.update { it.copy(actionBusy = false, errorMessage = "Error: ${e.message}") }
      }
    }
  }

  fun onPostular() {
    val current = _uiState.value.currentOffer ?: return
    viewModelScope.launch {
      _uiState.update { it.copy(actionBusy = true) }
      try {
        // 🔁 Modo prueba: no enviar swipe ni postulación
        // jobsApi.postSwipe(CandidateSwipeBody(current.id, "APPLY"))
        // val appResponse = jobsApi.postApplication(CandidateApplicationBody(current.id))
        // if (appResponse.isSuccessful) {
        moveToNextOffer()
        // } else {
        //     _uiState.update { it.copy(actionBusy = false, errorMessage = "Error al postular") }
        // }
      } catch (e: Exception) {
        _uiState.update { it.copy(actionBusy = false, errorMessage = "Error: ${e.message}") }
      }
    }
  }

  fun onGuardar() {
    // HU-24 opcional
    _uiState.update { it.copy(errorMessage = "Función Guardar aún no disponible") }
  }

  private fun moveToNextOffer() {
    val pending = _uiState.value.pendingOffers.toMutableList()
    if (pending.isNotEmpty()) {
      val next = pending.removeAt(0)
      _uiState.update {
        it.copy(
          currentOffer = next,
          pendingOffers = pending,
          actionBusy = false,
          errorMessage = null
        )
      }
      if (pending.size <= 2 && currentPage <= totalPages) {
        loadFeed(reset = false)
      }
    } else {
      if (currentPage <= totalPages) {
        loadFeed(reset = false)
      } else {
        _uiState.update {
          it.copy(
            currentOffer = null,
            empty = true,
            actionBusy = false,
            pendingOffers = emptyList()
          )
        }
      }
    }
  }

  private fun CandidateJobFeedItemJson.toJobOffer(): JobOffer {
    return JobOffer(
      id = jobId,
      title = title,
      company = companyName,
      communeName = communeName,
      modality = modality,
      salary = salaryOffered,
      experienceYears = yearsExperienceRequired,
      description = descriptionPreview,
      skills = skillsPreview.map { it.name },
      matchPercentage = match?.score?.toFloat(),
      externalImageUrl = externalImageUrl,
      hasProtectedJobImage = hasProtectedJobImage
    )
  }
}