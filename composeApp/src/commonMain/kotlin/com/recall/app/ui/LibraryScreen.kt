package com.recall.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.recall.app.domain.models.Deck
import com.recall.app.ui.components.DeckCard

/**
 * Responsive library screen that displays decks in an adaptive layout
 *
 * Layout behavior:
 * - Compact (< 600dp): Single column vertical list
 * - Medium (< 840dp): 2-column grid
 * - Expanded (≥ 840dp): 3-column grid
 *
 * @param windowSize Current window size category for responsive layout
 * @param decks List of decks to display
 * @param onDeckClick Callback when a deck is clicked
 * @param modifier Optional modifier for the screen
 */
@Composable
fun LibraryScreen(
    windowSize: WindowSize,
    decks: List<Deck>,
    onDeckClick: (Deck) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        Text(
            text = "My Library",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Adaptive content based on window size
        when (windowSize) {
            WindowSize.Compact -> {
                // Single column list for phones
                CompactLayout(decks = decks, windowSize = windowSize, onDeckClick = onDeckClick)
            }
            WindowSize.Medium -> {
                // 2-column grid for small tablets
                GridLayout(decks = decks, columns = 2, windowSize = windowSize, onDeckClick = onDeckClick)
            }
            WindowSize.Expanded -> {
                // 3-column grid for large tablets and desktop
                GridLayout(decks = decks, columns = 3, windowSize = windowSize, onDeckClick = onDeckClick)
            }
        }
    }
}

/**
 * Compact layout using LazyColumn for single-column vertical stacking
 */
@Composable
private fun CompactLayout(
    decks: List<Deck>,
    windowSize: WindowSize,
    onDeckClick: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        itemsIndexed(decks) { index, deck ->
            DeckCard(
                deck = deck,
                index = index,
                windowSize = windowSize,
                onClick = { onDeckClick(deck) }
            )
        }
    }
}

/**
 * Grid layout using LazyVerticalGrid for multi-column display
 *
 * @param columns Number of columns (2 for Medium, 3 for Expanded)
 */
@Composable
private fun GridLayout(
    decks: List<Deck>,
    columns: Int,
    windowSize: WindowSize,
    onDeckClick: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        itemsIndexed(decks) { index, deck ->
            DeckCard(
                deck = deck,
                index = index,
                windowSize = windowSize,
                onClick = { onDeckClick(deck) }
            )
        }
    }
}
