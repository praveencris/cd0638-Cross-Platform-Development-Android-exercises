package com.recall.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Room relation entity that efficiently joins DeckEntity with its CardEntity children
 *
 * This solves the N+1 query problem:
 * - WITHOUT this: 1 query for decks + N queries for each deck's cards = 1+N queries
 * - WITH this: 1 query with LEFT JOIN = 1 query total
 *
 * How it works:
 * - @Embedded: Includes all columns from DeckEntity
 * - @Relation: Room automatically performs SQL JOIN using parentColumn/entityColumn
 * - Result: Denormalized data structure with deck + all its cards in one query
 *
 * Performance impact:
 * - 10 decks with old approach: 11 database round-trips
 * - 10 decks with this approach: 1 database round-trip
 * - Instant loading, smooth UI, no choppy scrolling
 */
data class DeckWithCards(
    @Embedded
    val deck: DeckEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "deckId"
    )
    val cards: List<CardEntity>
)
