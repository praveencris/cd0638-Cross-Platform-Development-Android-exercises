package com.recall.app.domain.models

import kotlinx.serialization.Serializable

/**
 * Response wrapper for the decks JSON API
 * Contains metadata and the list of decks
 */
@Serializable
data class DecksResponse(
    val meta: Meta,
    val decks: List<Deck>
)

/**
 * Metadata information for the API response
 */
@Serializable
data class Meta(
    val version: Int,
    val author: String,
    val license: String
)
