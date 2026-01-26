package com.recall.app.domain.models

import kotlinx.datetime.Instant

/**
 * Represents the result of a study session/review
 *
 * @param cardId ID of the card that was reviewed
 * @param quality Quality rating given (0-5)
 * @param timestamp When the review occurred
 * @param previousInterval The interval before this review
 * @param newInterval The interval after this review
 */
data class ReviewLog(
    val cardId: String,
    val quality: Int,
    val timestamp: Instant,
    val previousInterval: Int,
    val newInterval: Int
)
