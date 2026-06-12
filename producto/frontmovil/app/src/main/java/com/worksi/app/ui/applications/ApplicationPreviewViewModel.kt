package com.worksi.app.ui.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.ApiErrorParser
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.CandidateApplicationDetailJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApplicationPreviewViewModel(private val applicationId: Long) : ViewModel() {
  private val api = RetrofitClient.candidateApplicationsApi

  private val _isLoading = MutableStateFlow(true)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val _detail = MutableStateFlow<CandidateApplicationDetailJson?>(null)
  val detail: StateFlow<CandidateApplicationDetailJson?> = _detail.asStateFlow()

  private val _cancelBusy = MutableStateFlow(false)
  val cancelBusy: StateFlow<Boolean> = _cancelBusy.asStateFlow()

  private val _cancelError = MutableStateFlow<String?>(null)
  val cancelError: StateFlow<String?> = _cancelError.asStateFlow()

  private val _cancelled = MutableStateFlow(false)
  val cancelled: StateFlow<Boolean> = _cancelled.asStateFlow()

  init {
    load()
  }

  fun retry() {
    load()
  }

  fun cancelApplication() {
    if (_cancelBusy.value || _cancelled.value) return
    viewModelScope.launch {
      _cancelBusy.value = true
      _cancelError.value = null
      val err =
          withContext(Dispatchers.IO) {
            try {
              val r = api.cancelApplication(applicationId)
              if (!r.isSuccessful && r.code() != 204) {
                ApiErrorParser.message(r)
              } else {
                null
              }
            } catch (e: Exception) {
              e.message ?: "Error de red"
            }
          }
      if (err != null) {
        _cancelError.value = err
      } else {
        _cancelled.value = true
      }
      _cancelBusy.value = false
    }
  }

  private fun load() {
    viewModelScope.launch {
      _isLoading.value = true
      _errorMessage.value = null
      try {
        val r = api.getApplication(applicationId)
        if (r.isSuccessful) {
          _detail.value = r.body()
        } else {
          _errorMessage.value = ApiErrorParser.message(r)
          _detail.value = null
        }
      } catch (e: Exception) {
        _errorMessage.value = e.message ?: "Error de red"
        _detail.value = null
      } finally {
        _isLoading.value = false
      }
    }
  }

  companion object {
    fun factory(applicationId: Long): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ApplicationPreviewViewModel(applicationId) as T
          }
        }
  }
}
