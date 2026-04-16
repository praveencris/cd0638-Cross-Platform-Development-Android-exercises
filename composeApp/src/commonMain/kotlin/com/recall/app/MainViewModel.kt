package com.recall.app

import com.recall.app.domain.models.Deck
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
 *
 * TODO: Implement data fetching logic in this ViewModel
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

    // TODO: Add an init block that calls fetchDecks() when the ViewModel is created
    // init {
    //     fetchDecks()
    // }

    // TODO: Implement fetchDecks() function
    // Hint: Use scope.launch to call apiService.fetchAllDecks() in a coroutine
    // Remember to update _isLoading, _decks, and _error states appropriately
    // private fun fetchDecks() {
    //     scope.launch {
    //         // Your implementation here
    //     }
    // }

    // TODO: Implement retry() function that calls fetchDecks()
    // fun retry() {
    //     fetchDecks()
    // }

    /**
     * Cancel work tied to this screen instance when the UI disposes it.
     */
    fun onCleared() {
        scope.cancel()
    }
}
