package com.recall.app.ui.previews

import com.recall.app.domain.models.Card
import com.recall.app.domain.models.Deck

/**
 * Sample data generator for Compose previews
 *
 * Provides reusable functions to create realistic sample data
 * for testing UI components in Android Studio preview mode.
 */

/**
 * Creates a sample card with realistic content
 */
fun sampleCard(
    id: String = "1",
    front: String = "Question",
    back: String = "Answer",
    ease: Float = 2.5f,
    interval: Int = 0
): Card {
    return Card(
        id = id,
        front = front,
        back = back,
        ease = ease,
        interval = interval,
        imageBase64 = null
    )
}

/**
 * Creates a sample deck with configurable properties
 */
fun sampleDeck(
    id: String = "1",
    title: String = "Sample Deck",
    colorHex: String = "#2B8CEE",
    cardCount: Int = 25
): Deck {
    return Deck(
        id = id,
        title = title,
        colorHex = colorHex,
        cards = List(cardCount) { index ->
            sampleCard(
                id = "$id-card-$index",
                front = "Question ${index + 1}",
                back = "Answer ${index + 1}",
                interval = if (index < cardCount / 2) 1 else 0 // Half reviewed
            )
        }
    )
}

/**
 * Creates a list of varied sample decks for previews
 */
fun sampleDeckList(): List<Deck> {
    return listOf(
        sampleDeck(
            id = "1",
            title = "Japanese N5",
            colorHex = "#2B8CEE",
            cardCount = 45
        ),
        sampleDeck(
            id = "2",
            title = "History 101",
            colorHex = "#F97316",
            cardCount = 32
        ),
        sampleDeck(
            id = "3",
            title = "Biology Terms",
            colorHex = "#10B981",
            cardCount = 68
        ),
        sampleDeck(
            id = "4",
            title = "Spanish Vocab",
            colorHex = "#2B8CEE",
            cardCount = 15
        ),
        sampleDeck(
            id = "5",
            title = "Chemistry",
            colorHex = "#F97316",
            cardCount = 52
        ),
        sampleDeck(
            id = "6",
            title = "Geography",
            colorHex = "#10B981",
            cardCount = 28
        ),
        sampleDeck(
            id = "7",
            title = "Programming",
            colorHex = "#2B8CEE",
            cardCount = 94
        ),
        sampleDeck(
            id = "8",
            title = "Music Theory",
            colorHex = "#F97316",
            cardCount = 38
        ),
        sampleDeck(
            id = "9",
            title = "Art History",
            colorHex = "#10B981",
            cardCount = 41
        )
    )
}

/**
 * Creates a small deck (< 20 cards) for testing low progress scenarios
 */
fun sampleSmallDeck(): Deck {
    return sampleDeck(
        id = "small",
        title = "French Basics",
        colorHex = "#2B8CEE",
        cardCount = 12
    )
}

/**
 * Creates a medium deck (20-50 cards) for testing moderate progress
 */
fun sampleMediumDeck(): Deck {
    return sampleDeck(
        id = "medium",
        title = "World Capitals",
        colorHex = "#F97316",
        cardCount = 35
    )
}

/**
 * Creates a large deck (> 50 cards) for testing high progress
 */
fun sampleLargeDeck(): Deck {
    return sampleDeck(
        id = "large",
        title = "Medical Terminology",
        colorHex = "#10B981",
        cardCount = 87
    )
}
