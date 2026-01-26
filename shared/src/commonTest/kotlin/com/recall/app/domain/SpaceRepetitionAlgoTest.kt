package com.recall.app.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpaceRepetitionAlgoTest {

    /**
     * Test that a failure (quality < 3) resets the interval to 1 day
     * regardless of the current interval
     */
    @Test
    fun failure_resets_streak() {
        // Start with a card that has been successfully reviewed multiple times
        val currentInterval = 15
        val currentEase = 2.5f
        val quality = 0 // Total failure

        val result = SpaceRepetitionAlgo.calculateNextReview(
            currentInterval = currentInterval,
            currentEase = currentEase,
            quality = quality
        )

        // Verify that the interval resets to 1 day
        assertEquals(1, result.nextInterval, "Failure should reset interval to 1 day")

        // Verify that the ease factor remains unchanged on failure
        assertEquals(currentEase, result.newEase, "Ease should remain unchanged on failure")
    }

    /**
     * Test that successful reviews increase the interval according to SM-2 algorithm
     * Progression: 0 -> 1 -> 6 -> exponential growth
     */
    @Test
    fun success_increases_interval() {
        val ease = 2.5f
        val quality = 5 // Perfect recall

        // First review: interval 0 -> 1
        val firstReview = SpaceRepetitionAlgo.calculateNextReview(
            currentInterval = 0,
            currentEase = ease,
            quality = quality
        )
        assertEquals(1, firstReview.nextInterval, "First review should give interval of 1 day")

        // Second review: interval 1 -> 6
        val secondReview = SpaceRepetitionAlgo.calculateNextReview(
            currentInterval = 1,
            currentEase = firstReview.newEase,
            quality = quality
        )
        assertEquals(6, secondReview.nextInterval, "Second review should give interval of 6 days")

        // Third review: interval should grow exponentially (6 * ease)
        val thirdReview = SpaceRepetitionAlgo.calculateNextReview(
            currentInterval = 6,
            currentEase = secondReview.newEase,
            quality = quality
        )
        assertTrue(
            thirdReview.nextInterval > 6,
            "Third review should have interval greater than 6 days"
        )
    }

    /**
     * Test that the ease factor never drops below the minimum threshold of 1.3
     * even with repeated poor performance
     */
    @Test
    fun ease_min_limit() {
        var ease = 1.5f
        val quality = 3 // Barely passing, which decreases ease

        // Simulate multiple barely-passing reviews to drive ease down
        repeat(20) {
            val result = SpaceRepetitionAlgo.calculateNextReview(
                currentInterval = 1,
                currentEase = ease,
                quality = quality
            )
            ease = result.newEase
        }

        // Verify that ease never drops below the minimum of 1.3
        assertTrue(
            ease >= 1.3f,
            "Ease factor should never drop below 1.3, but got $ease"
        )
    }
}
