package com.worksi.app.ui.jobdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.ApiErrorParser
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.CandidateJobDetailJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class JobDetailState {
  object Loading : JobDetailState()

  data class Error(val message: String) : JobDetailState()

  data class Ready(val detail: CandidateJobDetailJson) : JobDetailState()
}

class JobDetailViewModel(private val jobId: Long) : ViewModel() {
  private val api = RetrofitClient.candidateJobsApi

  private val _state = MutableStateFlow<JobDetailState>(JobDetailState.Loading)
  val state: StateFlow<JobDetailState> = _state.asStateFlow()

  init {
    viewModelScope.launch {
      load()
    }
  }

  fun retry() {
    viewModelScope.launch {
      load()
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
