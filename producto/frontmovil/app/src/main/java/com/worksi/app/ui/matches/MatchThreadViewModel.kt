package com.worksi.app.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.ApiErrorParser
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.CandidateConversationDetailJson
import com.worksi.app.data.model.MessageItemJson
import com.worksi.app.data.model.SendMessageRequestJson
import com.worksi.app.ui.components.CandidateUnreadChatsHolder
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MatchThreadViewModel(private val conversationId: Long) : ViewModel() {
  private val _header = MutableStateFlow<CandidateConversationDetailJson?>(null)
  val header: StateFlow<CandidateConversationDetailJson?> = _header.asStateFlow()

  private val _messages = MutableStateFlow<List<MessageItemJson>>(emptyList())
  val messages: StateFlow<List<MessageItemJson>> = _messages.asStateFlow()

  private val _loading = MutableStateFlow(true)
  val loading: StateFlow<Boolean> = _loading.asStateFlow()

  private val _sending = MutableStateFlow(false)
  val sending: StateFlow<Boolean> = _sending.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val lastMessageId = AtomicLong(0L)
  private val pollReady = AtomicBoolean(false)
  private var pollJob: Job? = null

  init {
    viewModelScope.launch {
      loadInitial()
      pollReady.set(true)
      startPolling()
    }
  }

  override fun onCleared() {
    pollJob?.cancel()
    CandidateUnreadChatsHolder.requestListRefresh()
    super.onCleared()
  }

  private suspend fun loadInitial() {
    _loading.value = true
    _errorMessage.value = null
    try {
      val detail = RetrofitClient.messagingApi.getConversation(conversationId)
      val page = RetrofitClient.messagingApi.listMessages(conversationId, page = 1, size = 50)
      if (!detail.isSuccessful) {
        _errorMessage.value = ApiErrorParser.message(detail)
        return
      }
      if (!page.isSuccessful) {
        _errorMessage.value = ApiErrorParser.message(page)
        return
      }
      _header.value = detail.body()
      applyMessages(page.body()?.items.orEmpty().sortedBy { it.messageId })
      CandidateUnreadChatsHolder.refresh()
      CandidateUnreadChatsHolder.requestListRefresh()
    } catch (e: Exception) {
      _errorMessage.value = e.message ?: "Error de red"
    } finally {
      _loading.value = false
    }
  }

  private fun applyMessages(items: List<MessageItemJson>) {
    _messages.value = items
    lastMessageId.set(items.maxOfOrNull { it.messageId } ?: 0L)
  }

  private fun mergeMessages(incoming: List<MessageItemJson>) {
    if (incoming.isEmpty()) return
    val merged =
        (_messages.value + incoming).distinctBy { it.messageId }.sortedBy { it.messageId }
    _messages.value = merged
    lastMessageId.set(merged.maxOfOrNull { it.messageId } ?: lastMessageId.get())
    viewModelScope.launch {
      CandidateUnreadChatsHolder.refresh()
      CandidateUnreadChatsHolder.requestListRefresh()
    }
  }

  private fun startPolling() {
    pollJob?.cancel()
    pollJob =
        viewModelScope.launch {
          while (isActive) {
            delay(CandidateUnreadChatsHolder.POLL_INTERVAL_MS)
            if (!pollReady.get()) continue
            pollNewMessages()
          }
        }
  }

  private suspend fun pollNewMessages() {
    try {
      val after = lastMessageId.get()
      val r =
          RetrofitClient.messagingApi.listMessages(
              conversationId, afterMessageId = after, size = 50)
      if (!r.isSuccessful) return
      mergeMessages(r.body()?.items.orEmpty())
    } catch (_: Exception) {
    }
  }

  fun sendMessage(body: String) {
    val trimmed = body.trim()
    if (trimmed.isEmpty() || trimmed.length > 500) return
    viewModelScope.launch {
      _sending.value = true
      _errorMessage.value = null
      try {
        val r =
            RetrofitClient.messagingApi.sendMessage(
                conversationId, SendMessageRequestJson(trimmed))
        if (r.isSuccessful) {
          val created = r.body() ?: return@launch
          mergeMessages(listOf(created))
        } else {
          _errorMessage.value = ApiErrorParser.message(r)
        }
      } catch (e: Exception) {
        _errorMessage.value = e.message ?: "Error de red"
      } finally {
        _sending.value = false
      }
    }
  }
}
