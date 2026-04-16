package com.recall.app.network

import com.recall.app.domain.models.Deck
import com.recall.app.domain.models.DecksResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * API service for fetching flashcard decks from the remote data source
 *
 * @param client The configured HttpClient instance
 */
class RecallApiService(private val client: HttpClient) {

    /**
     * Fetches all available decks from the remote API
     *
     * @return List of Deck objects with their cards
     * @throws Exception if the network request fails or JSON parsing fails
     */
    suspend fun fetchAllDecks(): List<Deck> {
        val response: DecksResponse = client.get(RecallApiConfig.decksUrl).body()
        return response.decks
    }
}
