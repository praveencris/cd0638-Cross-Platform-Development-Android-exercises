package com.recall.app.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.recall.app.domain.models.Deck
import com.recall.app.ui.LibraryScreen
import com.recall.app.ui.StudyScreen
import com.recall.app.ui.WindowSize

/**
 * Adaptive master-detail layout that provides different navigation patterns
 * based on screen size
 *
 * Layout behavior:
 * - **Compact (Phone)**: Stack navigation - shows either Library OR Study screen
 *   - Close button returns to library when study screen is shown
 * - **Medium (Portrait Tablet)**: Stack navigation - shows either Library OR Study screen
 *   - Same behavior as phone to avoid cramped split view
 * - **Expanded (Landscape Tablet/Desktop)**: Split view - shows Library AND Study simultaneously
 *   - Library pane on left (weight 1f) with single-column list
 *   - Vertical divider
 *   - Study pane on right (weight 2f)
 *
 * This pattern is standard for large-screen Android apps and allows the same
 * UI components to work seamlessly across different form factors.
 *
 * @param windowSize Current window size category for responsive layout
 * @param selectedDeck Currently selected deck (null means no selection)
 * @param decks List of all available decks
 * @param onDeckSelected Callback when a deck is selected or deselected
 * @param modifier Optional modifier for the layout
 */
@Composable
fun MasterDetailLayout(
    windowSize: WindowSize,
    selectedDeck: Deck?,
    decks: List<Deck>,
    onDeckSelected: (Deck?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Only use Split View for Expanded (Landscape Tablet/Desktop)
    // Compact (Phone) and Medium (Portrait Tablet) use CompactLayout (Stack)
    if (windowSize == WindowSize.Expanded) {
        SplitLayout(
            selectedDeck = selectedDeck,
            decks = decks,
            onDeckSelected = onDeckSelected,
            modifier = modifier
        )
    } else {
        CompactLayout(
            selectedDeck = selectedDeck,
            decks = decks,
            onDeckSelected = onDeckSelected,
            modifier = modifier
        )
    }
}

/**
 * Compact layout for phones - shows either library or study screen
 * Close button in StudyScreen navigates back to library
 */
@Composable
private fun CompactLayout(
    selectedDeck: Deck?,
    decks: List<Deck>,
    onDeckSelected: (Deck?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedDeck != null) {
        // Show study screen
        StudyScreen(
            deck = selectedDeck,
            onClose = { onDeckSelected(null) },
            modifier = modifier
        )
    } else {
        // Show library screen
        LibraryScreen(
            windowSize = WindowSize.Compact,
            decks = decks,
            onDeckClick = { deck -> onDeckSelected(deck) },
            modifier = modifier
        )
    }
}

/**
 * Split layout for tablets - shows library and study side by side
 * Forces Compact mode for library to render single-column list (not grid)
 */
@Composable
private fun SplitLayout(
    selectedDeck: Deck?,
    decks: List<Deck>,
    onDeckSelected: (Deck?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        // Left pane: Library
        // We force WindowSize.Compact so LibraryScreen renders as a LIST (1 col), not a GRID
        Box(modifier = Modifier.weight(1f)) {
            LibraryScreen(
                windowSize = WindowSize.Compact,
                decks = decks,
                onDeckClick = { deck -> onDeckSelected(deck) }
            )
        }

        // Divider
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )

        // Right pane: Study screen or empty state
        Box(modifier = Modifier.weight(2f)) {
            if (selectedDeck != null) {
                StudyScreen(
                    deck = selectedDeck,
                    onClose = { onDeckSelected(null) }
                )
            } else {
                // Empty state - could show a "Select a deck" message
                Box(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
