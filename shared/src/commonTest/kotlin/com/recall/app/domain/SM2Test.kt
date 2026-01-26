package com.recall.app.domain

import com.recall.app.domain.models.Card
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for SM-2 Spaced Repetition Algorithm
 */
class SM2Test {

    /**
     * Test that a "Good" (4) review on a card with interval 1
     * progresses to interval 6 (as per SM-2 algorithm)
     */
    @Test
    fun test_good_review_progresses_from_interval_1_to_6() {
        // Arrange: Create a card that's had one successful review (interval = 1)
        val card = Card(
            id = "test-card-1",
            front = "What is KMP?",
            back = "Kotlin Multiplatform",
            ease = 2.5f,
            interval = 1  // First review complete
        )

        // Act: Perform a "Good" (4) quality review
        val result = SpaceRepetitionAlgo.calculateNextReview(card, quality = 4)

        // Assert: The interval should progress to 6 days (second review milestone)
        assertEquals(
            expected = 6,
            actual = result.interval,
            message = "Good review on interval 1 should progress to interval 6"
        )

        // The ease factor should be adjusted based on quality 4
        assertTrue(
            actual = result.ease >= 1.3f,
            message = "Ease should remain above minimum threshold"
        )
    }

    /**
     * Test that an "Again" (0) review resets the interval to 1
     * regardless of the current interval
     */
    @Test
    fun test_again_review_resets_interval_to_1() {
        // Arrange: Create a card with a high interval (been reviewed many times successfully)
        val card = Card(
            id = "test-card-2",
            front = "Kotlin keyword for inheritance?",
            back = "open",
            ease = 2.8f,
            interval = 30  // Previously successful, long interval
        )

        // Act: User clicks "Again" (0) - failed to recall
        val result = SpaceRepetitionAlgo.calculateNextReview(card, quality = 0)

        // Assert: The interval should reset to 1 day
        assertEquals(
            expected = 1,
            actual = result.interval,
            message = "Failed review (Again) should reset interval to 1"
        )

        // The ease factor should remain unchanged on failure
        assertEquals(
            expected = 2.8f,
            actual = result.ease,
            message = "Ease should not change on failed review"
        )
    }

    /**
     * Test that ease factor never drops below the minimum threshold (1.3)
     */
    @Test
    fun test_ease_factor_minimum_threshold() {
        // Arrange: Create a card with ease close to minimum
        val card = Card(
            id = "test-card-3",
            front = "Test question",
            back = "Test answer",
            ease = 1.4f,  // Close to minimum
            interval = 5
        )

        // Act: Perform a "Hard" (2) quality review (should lower ease)
        val result = SpaceRepetitionAlgo.calculateNextReview(card, quality = 2)

        // Assert: Ease should not drop below 1.3
        assertTrue(
            actual = result.ease >= 1.3f,
            message = "Ease factor should never drop below 1.3"
        )
    }

    /**
     * Test interval progression from new card (interval 0) to first review (interval 1)
     */
    @Test
    fun test_new_card_first_review() {
        // Arrange: Create a brand new card (interval = 0)
        val card = Card(
            id = "new-card",
            front = "New question",
            back = "New answer",
            ease = 2.5f,
            interval = 0  // Brand new card
        )

        // Act: First successful review with quality "Good" (4)
        val result = SpaceRepetitionAlgo.calculateNextReview(card, quality = 4)

        // Assert: First review should set interval to 1 day
        assertEquals(
            expected = 1,
            actual = result.interval,
            message = "First successful review should set interval to 1 day"
        )
    }
}
