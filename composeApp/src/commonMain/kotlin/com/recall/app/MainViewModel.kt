package com.recall.app

import com.recall.app.domain.models.Deck
import com.recall.app.domain.Result
import com.recall.app.network.RecallApiService
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

    private val _selectedDeck = MutableStateFlow<Deck?>(null)
    val selectedDeck: StateFlow<Deck?> = _selectedDeck.asStateFlow()

    // Track current card index for each deck to support resume functionality
    private val deckProgress = mutableMapOf<String, Int>()

    init {
        fetchDecks()
    }

    fun fetchDecks() {
        scope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                when (val result = apiService.fetchAllDecks()) {
                    is Result.Success -> _decks.value = result.data
                    is Result.Error -> throw result.exception
                }
            } catch (e: kotlin.coroutines.cancellation.CancellationException) {
                throw e
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
        return deckProgress[deckId] ?: 0
    }

    /**
     * Update progress when a card is reviewed
     * @param deckId The deck's unique identifier
     * @param newIndex The new card index after review
     */
    fun onCardReviewed(deckId: String, newIndex: Int) {
        // Find the deck to get card count
        val deck = _decks.value.find { it.id == deckId } ?: return
        val cardCount = deck.cards.size

        // Reset progress if we've completed the deck (reached the end)
        val actualIndex = if (newIndex >= cardCount) 0 else newIndex

        // Save the card index for resume
        deckProgress[deckId] = actualIndex

        // Calculate progress fraction (0.0 to 1.0)
        val progressFraction = if (cardCount > 0) {
            actualIndex.toFloat() / cardCount.toFloat()
        } else {
            0f
        }

        // Update the deck in the list to trigger UI updates
        _decks.value = _decks.value.map { currentDeck ->
            if (currentDeck.id == deckId) {
                currentDeck.copy(progress = progressFraction)
            } else {
                currentDeck
            }
        }
    }

    fun onCleared() {
        scope.cancel()
    }
}
