package com.recall.app

import com.recall.app.data.RecallRepository
import com.recall.app.domain.models.Deck
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
 *
 * Offline-First Architecture:
 * - Always observes database via Repository (Single Source of Truth)
 * - Provides instant data on app launch (cached from previous session)
 * - Network refreshes update database silently in background
 * - UI automatically updates when database changes (reactive Flow)
 * - Works completely offline with cached data
 */
class MainViewModel(
    private val repository: RecallRepository
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

    // Progress is now persisted in database and loaded via repository
    // No in-memory map needed - Single Source of Truth

    init {
        // Observe database Flow for automatic UI updates
        // This provides instant data on app launch (offline-first)
        scope.launch {
            repository.getDecks().collect { decksFromDb ->
                _decks.value = decksFromDb
            }
        }
    }

    /**
     * Refresh decks from network and update database
     *
     * Offline-First Behavior:
     * - UI shows cached data immediately (from init block)
     * - This function triggers network sync in background
     * - Database Flow automatically updates UI when new data arrives
     * - Loading spinner shows sync progress, but doesn't block cached data
     *
     * Call this from UI pull-to-refresh or initial load trigger
     */
    fun fetchDecks() {
        scope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Fetch from network and update database
                // The Flow collection in init will automatically update _decks
                repository.refreshDecks()
            } catch (e: CancellationException) {
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

    /**
     * Clear the error state
     * Called after error is shown to user via snackbar
     */
    fun clearError() {
        _error.value = null
    }

    fun selectDeck(deck: Deck?) {
        _selectedDeck.value = deck
    }

    /**
     * Get the saved card index for a deck to support resume functionality
     * Now reads from database-loaded deck instead of in-memory map
     * @param deckId The deck's unique identifier
     * @return The saved card index, or 0 if deck not found
     */
    fun getStartIndex(deckId: String): Int {
        val deck = _decks.value.find { it.id == deckId } ?: return 0
        val cardCount = deck.cards.size
        if (cardCount == 0) return 0

        // Convert progress fraction back to card index
        return (deck.progress * cardCount).toInt().coerceIn(0, cardCount - 1)
    }

    /**
     * Update progress when a card is reviewed
     * Now persists to database instead of in-memory map
     * @param deckId The deck's unique identifier
     * @param newIndex The new card index after review
     */
    fun onCardReviewed(deckId: String, newIndex: Int) {
        scope.launch {
            // Find the deck to get card count
            val deck = _decks.value.find { it.id == deckId } ?: return@launch
            val cardCount = deck.cards.size

            // Reset progress if we've completed the deck (reached the end)
            val actualIndex = if (newIndex >= cardCount) 0 else newIndex

            // Persist to database instead of in-memory map
            repository.updateDeckProgress(deckId, actualIndex)

            // Database Flow will automatically update _decks with new progress
            // No need to manually update _decks here (reactive architecture)
        }
    }

    /**
     * Cancel screen-scoped work when this instance leaves composition.
     */
    fun onCleared() {
        scope.cancel()
    }
}
