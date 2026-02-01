package com.recall.app.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a deck/collection of flashcards
 *
 * @param id Unique identifier for the deck
 * @param title Display name of the deck
 * @param colorHex Hex color code for deck theming (e.g., "#FF5722")
 * @param category Category that determines which icon is displayed
 * @param cards List of cards in this deck
 * @param progress Current study progress as a fraction (0.0 to 1.0) for resume functionality
 */
@Serializable
data class Deck(
    val id: String,
    val title: String,
    @SerialName("color")
    val colorHex: String,
    val category: DeckCategory = DeckCategory.LANGUAGE,
    val cards: List<Card> = emptyList(),
    val progress: Float = 0f
)
