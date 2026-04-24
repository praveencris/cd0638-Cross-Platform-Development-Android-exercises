package com.recall.app.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import io.ktor.serialization.kotlinx.json.json
import io.ktor.http.headersOf
import io.ktor.http.HttpHeaders
import io.ktor.http.ContentType
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.HttpClient
import com.recall.app.domain.models.Meta
import com.recall.app.domain.models.DecksResponse
import com.recall.app.domain.models.Card
import com.recall.app.domain.Result
import com.recall.app.domain.models.Deck
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for RecallApiService
 * Tests JSON parsing and field mapping without hitting the live network.
 */
class RecallApiTest {

    private val testDecks = listOf(
        Deck(
            id = "deck-1",
            title = "NASA Space Gallery",
            colorHex = "#112233",
            cards = listOf(
                Card(id = "card-1", front = "Front 1", back = "Back 1")
            )
        )
    )

    @Test
    fun test_fetchAllDecks_returns_decks() = runTest {
        val apiService = createApiService()

        val decks = fetchDecks(apiService)

        assertTrue(
            actual = decks.isNotEmpty(),
            message = "Should parse at least one deck from the API response"
        )
    }

    @Test
    fun test_first_deck_title_is_nasa_space_gallery() = runTest {
        val apiService = createApiService()

        val decks = fetchDecks(apiService)

        val firstDeck = decks.firstOrNull()
        assertNotNull(firstDeck, "First deck should not be null")
        assertEquals(
            expected = "NASA Space Gallery",
            actual = firstDeck.title,
            message = "First deck title should be 'NASA Space Gallery'"
        )
    }

    @Test
    fun test_decks_contain_cards_with_data() = runTest {
        val apiService = createApiService()

        val decks = fetchDecks(apiService)

        val firstDeck = decks.first()
        assertTrue(
            actual = firstDeck.cards.isNotEmpty(),
            message = "First deck should contain cards"
        )

        val firstCard = firstDeck.cards.first()
        assertNotNull(firstCard.id, "Card should have an ID")
        assertNotNull(firstCard.front, "Card should have front text")
        assertNotNull(firstCard.back, "Card should have back text")
    }

    @Test
    fun test_deck_color_is_mapped_correctly() = runTest {
        val apiService = createApiService()

        val decks = fetchDecks(apiService)

        val firstDeck = decks.first()
        assertTrue(
            actual = firstDeck.colorHex.startsWith("#"),
            message = "Deck color should be a hex color code starting with #"
        )
    }


    private suspend fun fetchDecks(apiService: RecallApiService): List<Deck> {
        return when (val result = apiService.fetchAllDecks()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    private fun createApiService(): RecallApiService {
        val json = Json { ignoreUnknownKeys = true }
        val engine = MockEngine { request ->
            assertEquals(RecallApiConfig.decksUrl, request.url.toString())
            respond(
                content = json.encodeToString(
                    DecksResponse(
                        meta = Meta(version = 1, author = "test", license = "test"),
                        decks = testDecks
                    )
                ),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
        return RecallApiService(client)
    }
}
