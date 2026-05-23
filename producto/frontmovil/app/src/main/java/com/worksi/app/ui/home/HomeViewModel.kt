package com.worksi.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.ApiErrorParser
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.CandidateApplicationBody
import com.worksi.app.data.model.CandidateJobFeedItemJson
import com.worksi.app.data.model.CandidateSwipeBody
import com.worksi.app.data.model.JobOffer
import java.util.ArrayDeque
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {
  private val api = RetrofitClient.candidateJobsApi
  private val queue: ArrayDeque<CandidateJobFeedItemJson> = ArrayDeque()

  private val _isLoading = MutableStateFlow(true)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val _offer = MutableStateFlow<JobOffer?>(null)
  val offer: StateFlow<JobOffer?> = _offer.asStateFlow()

  private val _empty = MutableStateFlow(false)
  val empty: StateFlow<Boolean> = _empty.asStateFlow()

  private val _actionBusy = MutableStateFlow(false)
  val actionBusy: StateFlow<Boolean> = _actionBusy.asStateFlow()

  private val _showApplyConfirm = MutableStateFlow(false)
  val showApplyConfirm: StateFlow<Boolean> = _showApplyConfirm.asStateFlow()

  init {
    viewModelScope.launch {
      loadInitial()
    }
  }

  fun clearError() {
    _errorMessage.value = null
  }

  fun retry() {
    viewModelScope.launch {
      loadInitial()
    }
  }

  private suspend fun loadInitial() {
    _isLoading.value = true
    _errorMessage.value = null
    _empty.value = false
    queue.clear()
    val err =
        withContext(Dispatchers.IO) {
          try {
            val response = api.getFeed(page = 1, size = 20)
            if (!response.isSuccessful) {
              ApiErrorParser.message(response)
            } else {
              val body = response.body()
              if (body == null) {
                "Respuesta vacía"
              } else {
                body.items.forEach { queue.addLast(it) }
                null
              }
            }
          } catch (e: Exception) {
            e.message ?: "Error de red"
          }
        }
    _isLoading.value = false
    if (err != null) {
      _errorMessage.value = err
      syncFromQueue()
      return
    }
    syncFromQueue()
  }

  private fun syncFromQueue() {
    val head = queue.firstOrNull()
    _offer.value = head?.toJobOffer()
    _empty.value = queue.isEmpty() && _errorMessage.value == null
  }

  fun onSwipeToApply() {
    if (queue.firstOrNull() == null || _actionBusy.value) return
    _showApplyConfirm.value = true
  }

  fun onDismissApplyConfirm() {
    _showApplyConfirm.value = false
  }

  fun onConfirmApply() {
    val head = queue.firstOrNull() ?: return
    if (_actionBusy.value) return
    viewModelScope.launch {
      _actionBusy.value = true
      _showApplyConfirm.value = false
      _errorMessage.value = null
      val err =
          withContext(Dispatchers.IO) {
            try {
              val r = api.postApplication(CandidateApplicationBody(head.jobId))
              if (!r.isSuccessful) {
                ApiErrorParser.message(r)
              } else {
                null
              }
            } catch (e: Exception) {
              e.message ?: "Error de red"
            }
          }
      if (err != null) {
        _errorMessage.value = err
        _actionBusy.value = false
        return@launch
      }
      advanceQueue()
      _actionBusy.value = false
    }
  }

  fun onSwipeToPass() {
    swipePassAndAdvance()
  }

  private fun swipePassAndAdvance() {
    val head = queue.firstOrNull() ?: return
    if (_actionBusy.value) return
    viewModelScope.launch {
      _actionBusy.value = true
      _errorMessage.value = null
      val err =
          withContext(Dispatchers.IO) {
            try {
              val r = api.postSwipe(CandidateSwipeBody(head.jobId, "PASS"))
              if (!r.isSuccessful) {
                ApiErrorParser.message(r)
              } else {
                null
              }
            } catch (e: Exception) {
              e.message ?: "Error de red"
            }
          }
      if (err != null) {
        _errorMessage.value = err
        _actionBusy.value = false
        return@launch
      }
      advanceQueue()
      _actionBusy.value = false
    }
  }

  private suspend fun advanceQueue() {
    queue.removeFirst()
    syncFromQueue()
    if (queue.isEmpty()) {
      _isLoading.value = true
      val refillErr =
          withContext(Dispatchers.IO) {
            try {
              val response = api.getFeed(page = 1, size = 20)
              if (!response.isSuccessful) {
                ApiErrorParser.message(response)
              } else {
                val body = response.body()
                if (body == null) {
                  "Respuesta vacía"
                } else {
                  body.items.forEach { queue.addLast(it) }
                  null
                }
              }
            } catch (e: Exception) {
              e.message ?: "Error de red"
            }
          }
      _isLoading.value = false
      if (refillErr != null) {
        _errorMessage.value = refillErr
      }
    }
    syncFromQueue()
  }

  private fun CandidateJobFeedItemJson.toJobOffer(): JobOffer =
      JobOffer(
          id = jobId,
          title = title,
          company = companyName,
          communeName = communeName.ifBlank { "—" },
          modality = modality,
          salary = salaryOffered,
          experienceYears = yearsExperienceRequired,
          description = descriptionPreview,
          skills = skillsPreview.map { it.name },
          matchPercentage = match?.score?.toFloat()?.coerceIn(0f, 100f),
          externalImageUrl = externalImageUrl?.trim()?.takeIf { it.isNotEmpty() },
          hasProtectedJobImage = hasProtectedJobImage)
}
