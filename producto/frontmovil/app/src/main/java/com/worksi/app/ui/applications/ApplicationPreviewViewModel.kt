package com.worksi.app.ui.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.ApiErrorParser
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.CandidateApplicationDetailJson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApplicationPreviewViewModel(private val applicationId: Long) : ViewModel() {
  private val api = RetrofitClient.candidateApplicationsApi

  private val _isLoading = MutableStateFlow(true)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val _detail = MutableStateFlow<CandidateApplicationDetailJson?>(null)
  val detail: StateFlow<CandidateApplicationDetailJson?> = _detail.asStateFlow()

  init {
    load()
  }

  fun retry() {
    load()
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
