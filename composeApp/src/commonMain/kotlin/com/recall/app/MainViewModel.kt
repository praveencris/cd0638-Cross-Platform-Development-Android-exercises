package com.recall.app

// TODO: Uncomment after linking :shared module and adding DI dependencies
// import androidx.lifecycle.ViewModel
// import androidx.lifecycle.viewModelScope
// import com.recall.app.network.RecallApiService
// import kotlinx.coroutines.flow.MutableStateFlow
// import kotlinx.coroutines.flow.StateFlow
// import kotlinx.coroutines.flow.asStateFlow
// import kotlinx.coroutines.launch

/**
 * Main ViewModel demonstrating dependency injection
 * Receives RecallApiService from Koin DI
 */
// TODO: Uncomment after linking :shared module and adding DI dependencies
// class MainViewModel(
//     private val apiService: RecallApiService
// ) : ViewModel() {
//
//     private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
//     val uiState: StateFlow<UiState> = _uiState.asStateFlow()
//
//     fun fetchDecks() {
//         viewModelScope.launch {
//             _uiState.value = UiState.Loading
//             try {
//                 val decks = apiService.fetchAllDecks()
//                 _uiState.value = UiState.Success(decks.size)
//             } catch (e: kotlin.coroutines.cancellation.CancellationException) {
//                 throw e
//             } catch (e: Exception) {
//                 _uiState.value = UiState.Error(e.message ?: "Unknown error")
//             }
//         }
//     }
//
//     sealed class UiState {
//         data object Initial : UiState()
//         data object Loading : UiState()
//         data class Success(val deckCount: Int) : UiState()
//         data class Error(val message: String) : UiState()
//     }
// }
