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

                // Trigger data loading when the composable enters composition
                LaunchedEffect(Unit) {
                    viewModel.fetchDecks()
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        error != null -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Error: $error",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                Button(onClick = { viewModel.retry() }) {
                                    Text("Retry")
                                }
                            }
                        }
                        decks.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No decks available",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        else -> {
                            MasterDetailLayout(
                                windowSize = windowSize,
                                selectedDeck = selectedDeck,
                                decks = decks,
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
