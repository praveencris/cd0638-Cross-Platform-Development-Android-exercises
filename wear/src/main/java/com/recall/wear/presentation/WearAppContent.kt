package com.recall.wear.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onRetry: () -> Unit,
    onDeckClick: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: Replace this placeholder with ScalingLazyColumn to show the list of decks
    // Hint: Use Scaffold with TimeText at the top to show the clock
    // Hint: Use ScalingLazyColumn for the Fish-Eye effect on round screens
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Hello Wear OS")
    }
}

// TODO: Create a DeckChip component using Wear Compose
// Hint: Use Chip from androidx.wear.compose.material
// Hint: Set the label to deck.title
// Hint: Set secondaryLabel to "${deck.cards.size} cards"
// Hint: Use ChipDefaults.secondaryChipColors() for styling
