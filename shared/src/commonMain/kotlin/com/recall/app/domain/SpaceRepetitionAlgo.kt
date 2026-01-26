package com.recall.app.domain

import kotlin.math.roundToInt

/**
 * Data class representing the result of a spaced repetition calculation
 * @param nextInterval The number of days until the next review
 * @param newEase The updated ease factor for the card
 */
data class ReviewResult(
    val nextInterval: Int,
    val newEase: Float
)

/**
 * SM-2 Spaced Repetition Algorithm Implementation
 *
 * This algorithm calculates optimal review intervals based on user performance.
 * The algorithm adjusts both the interval between reviews and the ease factor
 * (difficulty multiplier) based on the quality of the user's recall.
 */
object SpaceRepetitionAlgo {

    private const val EASE_MIN = 1.3f

    /**
     * Calculate the next review interval and ease factor based on performance
     *
     * @param currentInterval Current interval in days (0 for new cards)
     * @param currentEase Current ease factor (typically starts at 2.5)
     * @param quality User's recall quality rating (0-5)
     *                0-2: Failure (incorrect recall)
     *                3-5: Success (correct recall with varying confidence)
     * @return ReviewResult containing the next interval and updated ease factor
     */
    fun calculateNextReview(
        currentInterval: Int,
        currentEase: Float,
        quality: Int
    ): ReviewResult {
        // If quality < 3, the user failed to recall correctly
        if (quality < 3) {
            // Reset interval to 1 day, but keep the ease factor
            return ReviewResult(
                nextInterval = 1,
                newEase = currentEase
            )
        }

        // User successfully recalled the card
        // Calculate the next interval based on the SM-2 algorithm
        val nextInterval = when (currentInterval) {
            0 -> 1          // First review: 1 day
            1 -> 6          // Second review: 6 days
            else -> (currentInterval * currentEase).roundToInt()  // Subsequent: exponential growth
        }

        // Calculate the new ease factor based on performance
        // Formula: ease + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))
        val easeAdjustment = 0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f)
        val newEase = (currentEase + easeAdjustment).coerceAtLeast(EASE_MIN)

        return ReviewResult(
            nextInterval = nextInterval,
            newEase = newEase
        )
    }
}
