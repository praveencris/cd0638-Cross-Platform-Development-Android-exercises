package com.recall.wear.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.recall.app.domain.models.Deck

/**
 * Main content composable for Wear OS app
 * Displays deck list with loading and error states
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
