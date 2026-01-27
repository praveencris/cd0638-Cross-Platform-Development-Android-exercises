package com.recall.app.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a flashcard in the spaced repetition system
 *
 * @param id Unique identifier for the card
 * @param front Front side of the card (question/prompt)
 * @param back Back side of the card (answer)
 * @param ease Ease factor for spaced repetition (default: 2.5)
 * @param interval Days until next review (default: 0 for new cards)
 * @param imageBase64 Optional base64-encoded image for the card
 */
@Serializable
data class Card(
    val id: String,
    val front: String,
    val back: String,
    val ease: Float = 2.5f,
    val interval: Int = 0,
    @SerialName("image_base64")
    val imageBase64: String? = null
)
