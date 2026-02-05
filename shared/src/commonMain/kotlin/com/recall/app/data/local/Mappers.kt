package com.recall.app.data.local

import com.recall.app.data.local.entity.CardEntity
import com.recall.app.data.local.entity.DeckEntity
import com.recall.app.domain.models.Card
import com.recall.app.domain.models.Deck
import com.recall.app.domain.models.DeckCategory

/**
 * Extension functions for mapping between database entities and domain models
 *
 * Why separate Entity and Domain models?
 * - Database schema can evolve independently from business logic
 * - Entities are optimized for SQL storage (foreign keys, indexes)
 * - Domain models include computed properties and UI-specific fields
 */

// ==================== Entity → Domain (Reading from Database) ====================

/**
 * Convert DeckEntity to Domain Deck model
 * @param cards List of cards belonging to this deck
 * @return Deck domain model with default values for fields not stored in database
 */
fun DeckEntity.toDomain(cards: List<Card> = emptyList()): Deck {
    // Calculate progress fraction from persisted currentCardIndex
    val progressFraction = if (cards.isNotEmpty()) {
        currentCardIndex.toFloat() / cards.size.toFloat()
    } else {
        0f
    }

    return Deck(
        id = id,
        title = title,
        colorHex = "#6200EE", // Default color - could be stored in DB in future
        category = try {
            DeckCategory.valueOf(category)
        } catch (e: IllegalArgumentException) {
            DeckCategory.LANGUAGE // Fallback if invalid category
        },
        cards = cards,
        progress = progressFraction // Now computed from persisted currentCardIndex
    )
}

/**
 * Convert CardEntity to Domain Card model
 * @return Card domain model
 */
fun CardEntity.toDomain(): Card = Card(
    id = id,
    front = front,
    back = back,
    ease = ease,
    interval = interval,
    imageBase64 = imageBase64
)

// ==================== Domain → Entity (Writing to Database) ====================

/**
 * Convert Domain Deck to DeckEntity
 * Only persists core fields needed for database storage
 * @return DeckEntity for database insertion
 */
fun Deck.toEntity(): DeckEntity {
    // Calculate currentCardIndex from progress fraction
    val cardIndex = if (cards.isNotEmpty()) {
        (progress * cards.size).toInt().coerceIn(0, cards.size - 1)
    } else {
        0
    }

    return DeckEntity(
        id = id,
        title = title,
        category = category.name, // Persist category to database
        currentCardIndex = cardIndex // Now persists progress
    )
}

/**
 * Convert Domain Card to CardEntity
 * @param deckId Foreign key to associate card with its deck
 * @return CardEntity for database insertion
 */
fun Card.toEntity(deckId: String): CardEntity = CardEntity(
    id = id,
    deckId = deckId,
    front = front,
    back = back,
    ease = ease,
    interval = interval,
    imageBase64 = imageBase64
)
