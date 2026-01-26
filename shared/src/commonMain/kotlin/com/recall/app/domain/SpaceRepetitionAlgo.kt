package com.recall.app.domain

import com.recall.app.domain.models.Card
import kotlin.math.roundToInt

/**
 * SM-2 Spaced Repetition Algorithm Implementation
 *
 * This algorithm calculates optimal review intervals for flashcards based on
 * user performance, helping optimize long-term memory retention.
 *
 * Quality Ratings:
 * - 0 (Again): Complete blackout, wrong response
 * - 2 (Hard): Correct response with serious difficulty
 * - 4 (Good): Correct response with some hesitation
 * - 5 (Easy): Perfect response
 */
object SpaceRepetitionAlgo {

    private const val EASE_MIN = 1.3f

    /**
     * Calculate the next review parameters for a card based on user performance
     *
     * @param card The current card with its interval and ease factor
     * @param quality User's recall quality rating (0, 2, 4, or 5)
     * @return Updated card with new interval and ease factor
     */
    fun calculateNextReview(card: Card, quality: Int): Card {
        TODO("Implement SM-2 Logic based on provided formula")
    }
}
