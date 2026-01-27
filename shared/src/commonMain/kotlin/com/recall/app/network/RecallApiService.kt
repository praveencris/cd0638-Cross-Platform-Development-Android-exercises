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
     * Base URL for the decks JSON data
     */
    private val baseUrl = "https://gist.githubusercontent.com/kshitizbali/7dba0064f63c3971d0194a6b5d938d56/raw/4b138fbc16292c86d2ada848738b0a706c29d5cc/decks.json"

    /**
     * Fetches all available decks from the remote API
     *
     * @return List of Deck objects with their cards
     * @throws Exception if the network request fails or JSON parsing fails
     */
    suspend fun fetchAllDecks(): List<Deck> {
        val response: DecksResponse = client.get(baseUrl).body()
        return response.decks
    }
}
