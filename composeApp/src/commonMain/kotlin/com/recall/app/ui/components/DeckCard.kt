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
    // TODO 1: Create Card with RoundedCornerShape(16.dp) and elevation
    //   Hint: Use Card(...) { Box(Modifier.fillMaxSize().background(...).padding(20.dp)) { ... } }

    // TODO 2: Add gradient background using a color palette based on index
    //   Hint: Brush.verticalGradient(listOf(gradientStart, gradientEnd))
    //   Define a list of color palettes and rotate: colorPalettes[index % colorPalettes.size]

    // TODO 3: Add deck title and card count text in a Column
    //   Hint: deck.title for title, "${deck.cards.size} cards" for count
    //   Use MaterialTheme.typography.titleLarge and bodyMedium

    // TODO 4: Add category icon with rounded background
    //   Hint: Use painterResource() with drawable resources for each category
    //   Wrap icon in a Box with CircleShape background

    // TODO 5: Add progress indicator bar at the bottom
    //   Hint: LinearProgressIndicator(progress = { calculateProgress(deck) })
    //   Write calculateProgress() as a placeholder returning 0.3f-0.85f based on card count
}
