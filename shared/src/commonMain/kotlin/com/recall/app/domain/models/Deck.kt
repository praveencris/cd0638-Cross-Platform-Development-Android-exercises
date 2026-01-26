package com.recall.app.domain.models

/**
 * Represents a deck/collection of flashcards
 *
 * @param id Unique identifier for the deck
 * @param title Display name of the deck
 * @param colorHex Hex color code for deck theming (e.g., "#FF5722")
 * @param cards List of cards in this deck
 */
data class Deck(
    val id: String,
    val title: String,
    val colorHex: String,
    val cards: List<Card> = emptyList()
)
