package com.recall.app.data.local.entity

// TODO: Import Room annotations for @Embedded and @Relation
// import androidx.room.Embedded
// import androidx.room.Relation

/**
 * Room relation entity that efficiently joins DeckEntity with its CardEntity children
 *
 * This solves the N+1 query problem:
 * - WITHOUT this: 1 query for decks + N queries for each deck's cards = 1+N queries
 * - WITH this: 1 query with LEFT JOIN = 1 query total
 *
 * TODO: Implement DeckWithCards using Room's @Embedded and @Relation annotations
 *
 * Hints:
 * - Use @Embedded on the deck field to include all DeckEntity columns
 * - Use @Relation on the cards field to JOIN CardEntity via parentColumn="id", entityColumn="deckId"
 * - This replaces the manual getCardsForDeck() call in the repository
 */
// data class DeckWithCards(
//     @Embedded
//     val deck: DeckEntity,
//
//     @Relation(
//         parentColumn = "id",
//         entityColumn = "deckId"
//     )
//     val cards: List<CardEntity>
// )
