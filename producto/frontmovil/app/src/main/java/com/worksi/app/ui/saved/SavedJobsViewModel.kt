package com.worksi.app.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.model.JobOffer
import com.worksi.app.data.saved.CandidateSavedJobsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SavedJobsUiState(
    val loading: Boolean = true,
    val errorMessage: String? = null,
    val items: List<JobOffer> = emptyList(),
    val busyJobId: Long? = null
)

class SavedJobsViewModel : ViewModel() {
    private val store = CandidateSavedJobsStore

    private val _ui = MutableStateFlow(SavedJobsUiState())
    val ui: StateFlow<SavedJobsUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            store.ensureLoaded()
        }
        viewModelScope.launch {
            combine(store.loading, store.errorMessage, store.items, store.busyJobId) {
                    loading,
                    errorMessage,
                    items,
                    busyJobId ->
                  SavedJobsUiState(
                      loading = loading,
                      errorMessage = errorMessage,
                      items = items,
                      busyJobId = busyJobId)
                }
                .collect { _ui.value = it }
        }
    }

    fun reload() {
        viewModelScope.launch {
            store.refreshFromApi()
        }
    }

    fun unsave(jobId: Long) {
        viewModelScope.launch {
            store.toggleSave(jobId)
        }
    }
}
