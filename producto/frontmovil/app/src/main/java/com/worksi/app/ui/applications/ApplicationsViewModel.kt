package com.worksi.app.ui.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.ApiErrorParser
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.CandidateApplicationListItemJson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApplicationsViewModel : ViewModel() {
  private val api = RetrofitClient.candidateApplicationsApi

  private val _isLoading = MutableStateFlow(true)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val _items = MutableStateFlow<List<CandidateApplicationListItemJson>>(emptyList())
  val items: StateFlow<List<CandidateApplicationListItemJson>> = _items.asStateFlow()

  init {
    load()
  }

  fun clearError() {
    _errorMessage.value = null
  }

  fun retry() {
    load()
  }

  private fun load() {
    viewModelScope.launch {
      _isLoading.value = true
      _errorMessage.value = null
      try {
        val r = api.listApplications(page = 1, size = 50)
        if (r.isSuccessful) {
          _items.value =
              r.body()?.items.orEmpty().sortedByDescending {
                it.matchScore ?: Double.NEGATIVE_INFINITY
              }
        } else {
          _errorMessage.value = ApiErrorParser.message(r)
          _items.value = emptyList()
        }
      } catch (e: Exception) {
        _errorMessage.value = e.message ?: "Error de red"
        _items.value = emptyList()
      } finally {
        _isLoading.value = false
      }
    }
  }
}
