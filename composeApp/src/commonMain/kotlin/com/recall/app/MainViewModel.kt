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

    // Track current card index for each deck to support resume functionality
    private val deckProgress = mutableMapOf<String, Int>()

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
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
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

    /**
     * Get the saved card index for a deck to support resume functionality
     * @param deckId The deck's unique identifier
     * @return The saved card index, or 0 if no progress exists
     */
    fun getStartIndex(deckId: String): Int {
        // TODO: Return saved index from deckProgress map (default to 0)
        return 0
    }

    /**
     * Update progress when a card is reviewed
     * @param deckId The deck's unique identifier
     * @param newIndex The new card index after review
     */
    fun onCardReviewed(deckId: String, newIndex: Int) {
        // TODO: Implement state tracking and list updates
        // 1. Find the deck to get card count
        // 2. Reset progress if completed (index >= cardCount)
        // 3. Save the card index in deckProgress
        // 4. Calculate progress fraction (index / cardCount)
        // 5. Update the deck in _decks list with new progress
    }
}
