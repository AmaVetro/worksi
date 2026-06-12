package com.worksi.app.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.ApiErrorParser
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.CandidateConversationListItemJson
import com.worksi.app.ui.components.CandidateUnreadChatsHolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MatchsViewModel : ViewModel() {
  private val _items = MutableStateFlow<List<CandidateConversationListItemJson>>(emptyList())
  val items: StateFlow<List<CandidateConversationListItemJson>> = _items.asStateFlow()

  private val _loading = MutableStateFlow(false)
  val loading: StateFlow<Boolean> = _loading.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private var pollJob: Job? = null
  private var hasLoadedOnce = false

  init {
    viewModelScope.launch {
      reload(showLoading = true)
      hasLoadedOnce = true
      startPolling()
    }
  }

  override fun onCleared() {
    pollJob?.cancel()
    super.onCleared()
  }

  fun reload(showLoading: Boolean = !hasLoadedOnce) {
    viewModelScope.launch {
      if (showLoading) {
        _loading.value = true
      }
      if (showLoading) {
        _errorMessage.value = null
      }
      try {
        val r = RetrofitClient.messagingApi.listConversations(page = 1, size = 50)
        if (r.isSuccessful) {
          _items.value = r.body()?.items.orEmpty()
          _errorMessage.value = null
          CandidateUnreadChatsHolder.refresh()
        } else if (showLoading) {
          _errorMessage.value = ApiErrorParser.message(r)
        }
      } catch (e: Exception) {
        if (showLoading) {
          _errorMessage.value = e.message ?: "Error de red"
        }
      } finally {
        if (showLoading) {
          _loading.value = false
        }
      }
    }
  }

  private fun startPolling() {
    pollJob?.cancel()
    pollJob =
        viewModelScope.launch {
          while (isActive) {
            delay(CandidateUnreadChatsHolder.POLL_INTERVAL_MS)
            reload(showLoading = false)
          }
        }
  }
}
