package com.recall.app.ui.previews

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.recall.app.ui.components.DeckCard

/**
 * Preview functions for DeckCard component
 *
 * Demonstrates different deck card variations with:
 * - Different color palettes (Blue, Orange, Emerald)
 * - Different deck sizes (small, medium, large)
 * - Various progress states
 */

@Preview(name = "Blue Palette Deck", showBackground = true)
@Composable
private fun DeckCardBluePreview() {
    DeckCard(
        deck = sampleDeck(
            title = "Japanese N5",
            colorHex = "#2B8CEE",
            cardCount = 45
        ),
        index = 0,
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(name = "Orange Palette Deck", showBackground = true)
@Composable
private fun DeckCardOrangePreview() {
    DeckCard(
        deck = sampleDeck(
            title = "History 101",
            colorHex = "#F97316",
            cardCount = 32
        ),
        index = 1,
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(name = "Emerald Palette Deck", showBackground = true)
@Composable
private fun DeckCardEmeraldPreview() {
    DeckCard(
        deck = sampleDeck(
            title = "Biology Terms",
            colorHex = "#10B981",
            cardCount = 68
        ),
        index = 2,
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(name = "Small Deck (Low Progress)", showBackground = true)
@Composable
private fun DeckCardSmallPreview() {
    DeckCard(
        deck = sampleSmallDeck(),
        index = 0,
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(name = "Medium Deck (Moderate Progress)", showBackground = true)
@Composable
private fun DeckCardMediumPreview() {
    DeckCard(
        deck = sampleMediumDeck(),
        index = 1,
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(name = "Large Deck (High Progress)", showBackground = true)
@Composable
private fun DeckCardLargePreview() {
    DeckCard(
        deck = sampleLargeDeck(),
        index = 2,
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(name = "Deck with Long Title", showBackground = true)
@Composable
private fun DeckCardLongTitlePreview() {
    DeckCard(
        deck = sampleDeck(
            title = "Advanced Organic Chemistry Reactions and Mechanisms",
            colorHex = "#2B8CEE",
            cardCount = 54
        ),
        index = 0,
        modifier = Modifier.padding(16.dp)
    )
}
