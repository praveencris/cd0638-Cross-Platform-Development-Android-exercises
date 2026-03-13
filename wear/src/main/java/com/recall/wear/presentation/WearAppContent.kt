package com.recall.wear.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.recall.app.domain.models.Deck
import com.recall.wear.datalayer.WearDataLayerService
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Main content composable for Wear OS app
 * Displays deck list with loading and error states
 * Includes a "Sync with Phone" chip that demonstrates the Wearable Data Layer API
 */
@Composable
fun WearAppContent(
    decks: List<Deck>,
    isLoading: Boolean,
    error: String?,
    heartRate: Int?,
    onRetry: () -> Unit,
    onDeckClick: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberScalingLazyListState()
    val dataLayerService: WearDataLayerService = koinInject()
    val coroutineScope = rememberCoroutineScope()
    var syncStatus by remember { mutableStateOf<String?>(null) }

    Scaffold(
        timeText = {
            TimeText()
        },
        modifier = modifier
    ) {
        ScalingLazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                top = 32.dp,
                bottom = 32.dp
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                ListHeader {
                    Text(
                        text = "Recall",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.title3
                    )
                }
            }

            // Display heart rate if available
            heartRate?.let { bpm ->
                item {
                    Text(
                        text = "❤️ $bpm BPM",
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }

            when {
                isLoading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                error != null -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Error loading decks",
                                style = MaterialTheme.typography.body2,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colors.error
                            )
                            Text(
                                text = error,
                                style = MaterialTheme.typography.caption3,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    item {
                        Chip(
                            label = { Text("Retry") },
                            onClick = onRetry,
                            colors = ChipDefaults.primaryChipColors()
                        )
                    }
                }

                decks.isEmpty() -> {
                    item {
                        Text(
                            text = "No decks available",
                            style = MaterialTheme.typography.body2,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }

                else -> {
                    items(decks) { deck ->
                        DeckChip(
                            deck = deck,
                            onClick = { onDeckClick(deck) }
                        )
                    }
                }
            }

            // Sync with Phone chip — Data Layer API demonstration
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Chip(
                    label = { Text(syncStatus ?: "Sync with Phone") },
                    onClick = {
                        coroutineScope.launch {
                            syncStatus = "Syncing..."
                            try {
                                // Send a refresh message to the phone
                                dataLayerService.sendRefreshRequest()
                                // Also sync current deck summary if we have data
                                if (decks.isNotEmpty()) {
                                    val firstDeck = decks.first()
                                    dataLayerService.syncDeckSummary(
                                        deckName = firstDeck.title,
                                        cardCount = decks.sumOf { it.cards.size }
                                    )
                                }
                                syncStatus = "Synced!"
                            } catch (e: Exception) {
                                syncStatus = "No phone found"
                            }
                        }
                    },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Show sync status briefly, then reset
            if (syncStatus == "Synced!" || syncStatus == "No phone found") {
                item {
                    LaunchedEffect(syncStatus) {
                        kotlinx.coroutines.delay(2000)
                        syncStatus = null
                    }
                }
            }
        }
    }
}

/**
 * Individual deck item as a Wear OS Chip
 */
@Composable
private fun DeckChip(
    deck: Deck,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Chip(
        label = {
            Text(
                text = deck.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        secondaryLabel = {
            Text(
                text = "${deck.cards.size} cards" +
                    if (deck.progress > 0f) " • ${(deck.progress * 100).toInt()}%" else "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        onClick = onClick,
        colors = ChipDefaults.secondaryChipColors(),
        modifier = modifier.fillMaxWidth()
    )
}
