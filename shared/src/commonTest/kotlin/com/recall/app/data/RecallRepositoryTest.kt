package com.recall.app.data

import com.recall.app.data.local.dao.RecallDao
import com.recall.app.data.local.entity.CardEntity
import com.recall.app.data.local.entity.DeckEntity
import com.recall.app.data.local.entity.DeckWithCards
import com.recall.app.domain.models.Card
import com.recall.app.domain.models.Deck
import com.recall.app.domain.models.DecksResponse
import com.recall.app.domain.models.Meta
import com.recall.app.network.RecallApiConfig
import com.recall.app.network.RecallApiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RecallRepositoryTest {

    @Test
    fun refreshDecks_preservesExistingProgress_whenNetworkDataReplacesDecks() = runTest {
        val existingDeck = DeckEntity(
            id = "deck-1",
            title = "Existing",
            category = "LANGUAGE",
            currentCardIndex = 2
        )
        val apiDeck = Deck(
            id = "deck-1",
            title = "Updated",
            colorHex = "#FF0000",
            cards = listOf(
                Card(id = "card-1", front = "Front 1", back = "Back 1"),
                Card(id = "card-2", front = "Front 2", back = "Back 2"),
                Card(id = "card-3", front = "Front 3", back = "Back 3")
            )
        )
        val dao = FakeRecallDao(existingDecks = listOf(existingDeck))
        val repository = RecallRepositoryImpl(
            api = createApiService(listOf(apiDeck)),
            dao = dao
        )

        repository.refreshDecks()

        val insertedDeck = dao.insertedDecks.single()
        val insertedCards = dao.insertedCardBatches.single()

        assertEquals(2, insertedDeck.currentCardIndex)
        assertEquals(apiDeck.id, insertedDeck.id)
        assertEquals(apiDeck.cards.size, insertedCards.size)
        assertTrue(insertedCards.all { it.deckId == apiDeck.id })
    }

    @Test
    fun refreshDecks_defaultsProgressToZero_forNewDecks() = runTest {
        val apiDeck = Deck(
            id = "deck-new",
            title = "Brand New",
            colorHex = "#00FF00",
            cards = listOf(Card(id = "card-1", front = "Front", back = "Back"))
        )
        val dao = FakeRecallDao()
        val repository = RecallRepositoryImpl(
            api = createApiService(listOf(apiDeck)),
            dao = dao
        )

        repository.refreshDecks()

        val insertedDeck = dao.insertedDecks.single()
        assertEquals(0, insertedDeck.currentCardIndex)
    }

    @Test
    fun refreshDecks_savesCardsForFetchedDecks() = runTest {
        val apiDeck = Deck(
            id = "deck-save",
            title = "Save Cards",
            colorHex = "#0000FF",
            cards = listOf(
                Card(id = "card-a", front = "A", back = "1"),
                Card(id = "card-b", front = "B", back = "2")
            )
        )
        val dao = FakeRecallDao()
        val repository = RecallRepositoryImpl(
            api = createApiService(listOf(apiDeck)),
            dao = dao
        )

        repository.refreshDecks()

        val insertedCards = dao.insertedCardBatches.single()
        assertEquals(listOf("card-a", "card-b"), insertedCards.map { it.id })
        assertNotNull(insertedCards.firstOrNull())
    }

    private fun createApiService(decks: List<Deck>): RecallApiService {
        val json = Json { ignoreUnknownKeys = true }
        val engine = MockEngine { request ->
            assertEquals(RecallApiConfig.decksUrl, request.url.toString())
            respond(
                content = json.encodeToString(
                    DecksResponse(
                        meta = Meta(version = 1, author = "test", license = "test"),
                        decks = decks
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

private class FakeRecallDao(
    existingDecks: List<DeckEntity> = emptyList()
) : RecallDao {
    private val decksFlow = MutableStateFlow(existingDecks)

    val insertedDecks = mutableListOf<DeckEntity>()
    val insertedCardBatches = mutableListOf<List<CardEntity>>()

    override fun getDecks(): Flow<List<DeckEntity>> = decksFlow.asStateFlow()

    override fun getDecksWithCards(): Flow<List<DeckWithCards>> =
        MutableStateFlow(emptyList<DeckWithCards>()).asStateFlow()

    override suspend fun insertDeck(deck: DeckEntity) {
        insertedDecks += deck
        decksFlow.value = decksFlow.value.filterNot { it.id == deck.id } + deck
    }

    override suspend fun insertCards(cards: List<CardEntity>) {
        insertedCardBatches += cards
    }

    override suspend fun deleteCardsForDeck(deckId: String) = Unit

    override suspend fun deleteCardsNotInDeckIds(deckIds: List<String>) = Unit

    override suspend fun deleteDecksNotIn(deckIds: List<String>) {
        decksFlow.value = decksFlow.value.filter { it.id in deckIds }
    }

    override suspend fun deleteAllCards() = Unit

    override suspend fun deleteAllDecks() {
        decksFlow.value = emptyList()
    }

    override suspend fun getCardsForDeck(deckId: String): List<CardEntity> = emptyList()

    override suspend fun updateDeckProgress(deckId: String, cardIndex: Int) {
        decksFlow.value = decksFlow.value.map { deck ->
            if (deck.id == deckId) deck.copy(currentCardIndex = cardIndex) else deck
        }
    }
}
