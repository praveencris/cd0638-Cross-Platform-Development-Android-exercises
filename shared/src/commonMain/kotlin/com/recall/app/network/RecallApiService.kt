package com.recall.app.network

import com.recall.app.domain.Result
import com.recall.app.domain.models.Deck
import com.recall.app.domain.models.DecksResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlin.coroutines.cancellation.CancellationException

/**
 * API service for fetching flashcard decks from the remote data source
 *
 * @param client The configured HttpClient instance
 */
open class RecallApiService(private val client: HttpClient) {

    /**
     * Fetches all available decks from the remote API
     *
     * Cancellation is rethrown so callers can stop in-flight work cleanly.
     */
    open suspend fun fetchAllDecks(): Result<List<Deck>> {
        return try {
            val response: DecksResponse = client.get(RecallApiConfig.decksUrl).body()
            Result.Success(response.decks)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
