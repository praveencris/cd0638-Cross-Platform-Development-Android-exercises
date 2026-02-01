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
 */
@Serializable
data class Deck(
    val id: String,
    val title: String,
    @SerialName("color")
    val colorHex: String,
    val category: DeckCategory = DeckCategory.LANGUAGE,
    val cards: List<Card> = emptyList()
)
