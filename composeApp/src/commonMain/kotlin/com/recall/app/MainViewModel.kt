package com.recall.app

import com.recall.app.domain.models.Deck
import com.recall.app.network.RecallApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks: StateFlow<List<Deck>> = _decks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedDeck = MutableStateFlow<Deck?>(null)
    val selectedDeck: StateFlow<Deck?> = _selectedDeck.asStateFlow()

    init {
        fetchDecks()
    }

    private fun fetchDecks() {
        scope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val fetchedDecks = apiService.fetchAllDecks()
                _decks.value = fetchedDecks
            } catch (e: kotlin.coroutines.cancellation.CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retry() {
        fetchDecks()
    }

    fun selectDeck(deck: Deck?) {
        _selectedDeck.value = deck
    }
}
