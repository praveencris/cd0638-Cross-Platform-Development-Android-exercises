package com.recall.app

import com.recall.app.domain.Result
import com.recall.app.domain.models.Deck
import com.recall.app.network.RecallApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Shared ViewModel for managing deck state across platforms
 * Fetches decks from the API and exposes them via StateFlow
 */
class MainViewModel(
    private val apiService: RecallApiService
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks: StateFlow<List<Deck>> = _decks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchDecks()
    }

    private fun fetchDecks() {
        scope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                when (val result = apiService.fetchAllDecks()) {
                is Result.Success -> {
                    _decks.value = result.data
                }
                is Result.Error -> {
                    val exception = result.exception
                    if (exception is CancellationException) {
                        throw exception
                    }
                    _error.value = result.message
                }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retry() {
        fetchDecks()
    }

    /**
     * Cancel work tied to this screen instance when the composable leaves composition.
     */
    fun onCleared() {
        scope.cancel()
    }
}
