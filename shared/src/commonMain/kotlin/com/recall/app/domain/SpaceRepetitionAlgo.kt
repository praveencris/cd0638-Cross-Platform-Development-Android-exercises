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
        // If quality < 3 (failure), reset interval to 1 day
        if (quality < 3) {
            return card.copy(
                interval = 1,
                // Ease stays the same on failure
                ease = card.ease
            )
        }

        // Quality >= 3 (success), progress the interval
        val nextInterval = when (card.interval) {
            0 -> 1      // First review: 1 day
            1 -> 6      // Second review: 6 days
            else -> (card.interval * card.ease).roundToInt()  // Subsequent: exponential growth
        }

        // Calculate new ease factor based on performance
        // Formula: ease + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))
        val easeAdjustment = 0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f)
        val newEase = (card.ease + easeAdjustment).coerceAtLeast(EASE_MIN)

        return card.copy(
            interval = nextInterval,
            ease = newEase
        )
    }
}
