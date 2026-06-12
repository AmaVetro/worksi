package com.worksi.app.ui.jobdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.ApiErrorParser
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.CandidateJobDetailJson
import com.worksi.app.data.saved.CandidateSavedJobsStore
import com.worksi.app.data.saved.toSavedJobOffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class JobDetailState {
  object Loading : JobDetailState()

  data class Error(val message: String) : JobDetailState()

  data class Ready(val detail: CandidateJobDetailJson) : JobDetailState()
}

class JobDetailViewModel(private val jobId: Long) : ViewModel() {
  private val api = RetrofitClient.candidateJobsApi
  private val store = CandidateSavedJobsStore

  private val _state = kotlinx.coroutines.flow.MutableStateFlow<JobDetailState>(JobDetailState.Loading)
  val state: StateFlow<JobDetailState> = _state

  val isSaved: StateFlow<Boolean> =
      store.savedJobIds
          .map { it.contains(jobId) }
          .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), store.isSaved(jobId))

  private val _saveError = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
  val saveError: StateFlow<String?> = _saveError

  val saveBusy: StateFlow<Boolean> =
      store.busyJobId
          .map { it == jobId }
          .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  init {
    viewModelScope.launch {
      store.ensureLoaded()
      load()
    }
  }

  fun retry() {
    viewModelScope.launch {
      load()
    }
  }

  fun toggleSaveJob() {
    if (store.busyJobId.value != null) return
    val offerForList =
        when (val s = _state.value) {
          is JobDetailState.Ready -> s.detail.toSavedJobOffer()
          else -> null
        }
    viewModelScope.launch {
      _saveError.value = null
      val err = store.toggleSave(jobId, offerForList)
      if (err != null) {
        _saveError.value = err
      }
    }
  }

  private suspend fun load() {
    _state.value = JobDetailState.Loading
    val err =
        withContext(Dispatchers.IO) {
          try {
            val response = api.getJobDetail(jobId)
            if (!response.isSuccessful) {
              ApiErrorParser.message(response)
            } else {
              val body = response.body()
              if (body == null) {
                "Respuesta vacía"
              } else {
                _state.value = JobDetailState.Ready(body)
                null
              }
            }
          } catch (e: Exception) {
            e.message ?: "Error de red"
          }
        }
    if (err != null) {
      _state.value = JobDetailState.Error(err)
    }
  }

  companion object {
    fun factory(jobId: Long) =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T =
              JobDetailViewModel(jobId) as T
        }
  }
}
