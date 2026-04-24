package com.recall.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.recall.app.ui.AdaptiveLayout
import com.recall.app.ui.navigation.MasterDetailLayout
import org.koin.compose.KoinContext
import org.koin.mp.KoinPlatform

/**
 * Shared Compose UI for both Android and iOS
 * Displays a responsive library of decks with adaptive layouts
 */
@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            AdaptiveLayout { windowSize ->
                val viewModel = remember { KoinPlatform.getKoin().get<MainViewModel>() }
                DisposableEffect(viewModel) {
                    onDispose {
                        viewModel.onCleared()
                    }
                }
                val decks by viewModel.decks.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                val error by viewModel.error.collectAsState()
                val selectedDeck by viewModel.selectedDeck.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                // Show error as snackbar (non-blocking)
                LaunchedEffect(error) {
                    error?.let {
                        val result = snackbarHostState.showSnackbar(
                            message = it,
                            actionLabel = "Retry",
                            duration = SnackbarDuration.Long
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.retry()
                        }
                        viewModel.clearError()
                    }
                }

                // Trigger initial data loading
                LaunchedEffect(Unit) {
                    viewModel.fetchDecks()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if (decks.isEmpty()) {
                            // Empty state - only shown when truly no data
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator()
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    Text(
                                        text = if (isLoading) "Loading..." else "No decks available",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        } else {
                            // Show decks with pull-to-refresh
                            MasterDetailLayout(
                                windowSize = windowSize,
                                selectedDeck = selectedDeck,
                                decks = decks,
                                isRefreshing = isLoading,
                                onRefresh = viewModel::fetchDecks,
                                onDeckSelected = viewModel::selectDeck,
                                getStartIndex = viewModel::getStartIndex,
                                onCardReviewed = viewModel::onCardReviewed
                            )
                        }
                    }
                }
            }
        }
    }
}