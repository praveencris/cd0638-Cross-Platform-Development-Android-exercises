package com.recall.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.recall.app.domain.models.Deck

/**
 * Beautiful gradient card component for displaying deck information
 *
 * @param deck The deck data to display
 * @param index The deck's position in the list (for color palette rotation)
 * @param modifier Optional modifier for the card
 */
@Composable
fun DeckCard(
    deck: Deck,
    index: Int,
    modifier: Modifier = Modifier
) {
    // TODO: Implement Card UI matching 'design/main_list_zen_study'
}
