package com.recall.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recall.app.network.RecallApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Main ViewModel demonstrating dependency injection
 * Receives RecallApiService from Koin DI
 */
class MainViewModel(
    private val apiService: RecallApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Fetch decks from the API
     */
    fun fetchDecks() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val decks = apiService.fetchAllDecks()
                _uiState.value = UiState.Success(decks.size)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * UI State sealed class
     */
    sealed class UiState {
        data object Initial : UiState()
        data object Loading : UiState()
        data class Success(val deckCount: Int) : UiState()
        data class Error(val message: String) : UiState()
    }
}
