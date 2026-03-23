package com.recall.app.network

import com.recall.app.domain.Result
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
open class RecallApiService(private val client: HttpClient) {

    /**
     * Base URL for the decks JSON data
     */
    private val baseUrl = "https://gist.githubusercontent.com/kshitizbali/1d894000650f1a1836ab8ad586a39f3b/raw/8200d74f96660bcd828d4cdead158f512fc17289/decks.json"

    /**
     * Fetches all available decks from the remote API
     *
     * @return Result.Success with the list of decks, or Result.Error on failure
     */
    open suspend fun fetchAllDecks(): Result<List<Deck>> {
        return try {
            val response: DecksResponse = client.get(baseUrl).body()
            Result.Success(response.decks)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
