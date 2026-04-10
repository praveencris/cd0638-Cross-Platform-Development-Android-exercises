package com.recall.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.recall.app.domain.models.Deck

/**
 * Responsive library screen that displays decks in an adaptive layout
 *
 * @param windowSize Current window size category for responsive layout
 * @param decks List of decks to display
 * @param modifier Optional modifier for the screen
 */
@Composable
fun LibraryScreen(
    windowSize: WindowSize,
    decks: List<Deck>,
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

        // TODO: Implement adaptive layout based on windowSize
        //   - Compact: Use LazyColumn for single-column vertical list
        //   - Medium: Use LazyVerticalGrid with GridCells.Fixed(2)
        //   - Expanded: Use LazyVerticalGrid with GridCells.Fixed(3)
        //   Hint: Use when(windowSize) { ... } and render DeckCard for each deck
    }
}
