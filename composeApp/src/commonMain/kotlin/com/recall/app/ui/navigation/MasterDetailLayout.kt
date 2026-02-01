package com.recall.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.recall.app.domain.models.Deck
import com.recall.app.ui.WindowSize

/**
 * Adaptive master-detail layout that provides different navigation patterns
 * based on screen size
 *
 * Layout behavior:
 * - **Compact (Phone)**: Stack navigation - shows either Library OR Study screen
 * - **Medium/Expanded (Tablet)**: Split view - shows Library AND Study simultaneously
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
    // TODO: Implement "List-Detail" pattern based on WindowSize
    // Hint: If Expanded, show both Side-by-Side. If Compact, show only one.
}
