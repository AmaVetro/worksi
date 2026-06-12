package com.worksi.app.data.saved

import com.worksi.app.data.api.ApiErrorParser
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.CandidateJobDetailJson
import com.worksi.app.data.model.CandidateJobFeedItemJson
import com.worksi.app.data.model.CandidateSavedJobBody
import com.worksi.app.data.model.JobOffer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object CandidateSavedJobsStore {
  private val api = RetrofitClient.candidateJobsApi
  private val mutex = Mutex()

  private val _savedJobIds = MutableStateFlow<Set<Long>>(emptySet())
  val savedJobIds: StateFlow<Set<Long>> = _savedJobIds.asStateFlow()

  private val _items = MutableStateFlow<List<JobOffer>>(emptyList())
  val items: StateFlow<List<JobOffer>> = _items.asStateFlow()

  private val _loading = MutableStateFlow(false)
  val loading: StateFlow<Boolean> = _loading.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val _busyJobId = MutableStateFlow<Long?>(null)
  val busyJobId: StateFlow<Long?> = _busyJobId.asStateFlow()

  private var loaded = false

  private fun sortByMatchScore(offers: List<JobOffer>): List<JobOffer> =
      offers.sortedByDescending { it.matchPercentage ?: Float.NEGATIVE_INFINITY }

  fun isSaved(jobId: Long): Boolean = _savedJobIds.value.contains(jobId)

  suspend fun ensureLoaded() {
    if (loaded) return
    refreshFromApi()
  }

  suspend fun refreshFromApi() {
    mutex.withLock {
      _loading.value = true
      _errorMessage.value = null
      val result = fetchSavedOffers()
      if (result.isFailure) {
        _errorMessage.value = result.exceptionOrNull()?.message ?: "Error de red"
        _items.value = emptyList()
        _savedJobIds.value = emptySet()
      } else {
        val offers = sortByMatchScore(result.getOrThrow())
        _items.value = offers
        _savedJobIds.value = offers.map { it.id }.toSet()
        loaded = true
      }
      _loading.value = false
    }
  }

  suspend fun toggleSave(jobId: Long, offerForList: JobOffer? = null): String? {
    if (_busyJobId.value != null) return null
    val currentlySaved = _savedJobIds.value.contains(jobId)
    _busyJobId.value = jobId
    _errorMessage.value = null
    val err =
        try {
          val response =
              if (currentlySaved) {
                api.unsaveJob(jobId)
              } else {
                api.saveJob(CandidateSavedJobBody(jobId))
              }
          if (currentlySaved) {
            if (!response.isSuccessful && response.code() != 204) {
              ApiErrorParser.message(response)
            } else {
              null
            }
          } else if (!response.isSuccessful && response.code() != 201 && response.code() != 204) {
            ApiErrorParser.message(response)
          } else {
            null
          }
        } catch (e: Exception) {
          e.message ?: "Error de red"
        }
    if (err == null) {
      if (currentlySaved) {
        _savedJobIds.value = _savedJobIds.value - jobId
        _items.value = _items.value.filter { it.id != jobId }
      } else {
        _savedJobIds.value = _savedJobIds.value + jobId
        val offer = offerForList ?: _items.value.firstOrNull { it.id == jobId }
        if (offer != null) {
          _items.value = sortByMatchScore(listOf(offer) + _items.value.filter { it.id != jobId })
        } else {
          val refill = fetchSavedOffers()
          if (refill.isSuccess) {
            val offers = sortByMatchScore(refill.getOrThrow())
            _items.value = offers
            _savedJobIds.value = offers.map { it.id }.toSet()
            loaded = true
          }
        }
      }
    }
    _busyJobId.value = null
    return err
  }

  fun clear() {
    _savedJobIds.value = emptySet()
    _items.value = emptyList()
    _loading.value = false
    _errorMessage.value = null
    _busyJobId.value = null
    loaded = false
  }

  private suspend fun fetchSavedOffers(): Result<List<JobOffer>> =
      try {
        val response = api.getSavedJobs(page = 1, size = 100)
        if (!response.isSuccessful) {
          Result.failure(Exception(ApiErrorParser.message(response)))
        } else {
          val body = response.body()
          if (body == null) {
            Result.failure(Exception("Respuesta vacía"))
          } else {
            Result.success(body.items.map { it.toJobOffer() })
          }
        }
      } catch (e: Exception) {
        Result.failure(e)
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

fun CandidateJobDetailJson.toSavedJobOffer(): JobOffer =
    JobOffer(
        id = jobId,
        title = title,
        company = companyName,
        communeName = communeName.ifBlank { "—" },
        modality = modality,
        salary = salaryOffered,
        experienceYears = yearsExperienceRequired,
        description = description,
        skills = skills.map { it.name },
        matchPercentage = match?.score?.toFloat()?.coerceIn(0f, 100f),
        externalImageUrl = externalImageUrl?.trim()?.takeIf { it.isNotEmpty() },
        hasProtectedJobImage = hasProtectedJobImage)
