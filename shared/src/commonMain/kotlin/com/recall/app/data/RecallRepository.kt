package com.recall.app.data

import com.recall.app.data.local.dao.RecallDao
import com.recall.app.data.local.toDomain
import com.recall.app.data.local.toEntity
import com.recall.app.domain.Result
import com.recall.app.domain.models.Deck
import com.recall.app.network.RecallApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Repository interface for Offline-First architecture
 *
 * Key Principles:
 * - Single Source of Truth: Room Database
 * - UI always observes database (instant data, no loading spinners for cached data)
 * - Network fetches only write to database, never return directly to UI
 * - App works offline with cached data
 *
 * Data Flow:
 * 1. UI observes getDecks() Flow → always returns from database
 * 2. UI triggers refreshDecks() → fetches from network and updates database
 * 3. Database change → Flow automatically emits → UI updates
 */
interface RecallRepository {
    /**
     * Observe all decks from database (Single Source of Truth)
     *
     * This Flow:
     * - Returns cached data immediately (instant app launch)
     * - Automatically emits when database changes (reactive updates)
     * - Works offline (no network required)
     *
     * @return Flow that emits list of decks whenever database changes
     */
    fun getDecks(): Flow<List<Deck>>

    /**
     * Fetch fresh data from network and update database
     *
     * This function:
     * - Fetches from network (requires connectivity)
     * - Updates local database with fresh data
     * - Database Flow automatically notifies observers
     * - UI updates silently in background
     *
     * Throws exception on network error - let ViewModel handle error state
     */
    suspend fun refreshDecks()

    /**
     * Update the current card index for a deck (for resume functionality)
     *
     * This function:
     * - Updates database directly (Single Source of Truth)
     * - Database Flow automatically notifies observers
     * - Progress persists across app restarts
     *
     * @param deckId The deck's unique identifier
     * @param cardIndex The current card index (0-based)
     */
    suspend fun updateDeckProgress(deckId: String, cardIndex: Int)
}

/**
 * Implementation of RecallRepository using Room Database and Ktor API
 */
class RecallRepositoryImpl(
    private val api: RecallApiService,
    private val dao: RecallDao
) : RecallRepository {
    override fun getDecks(): Flow<List<Deck>> {
        // PERFORMANCE OPTIMIZATION (Module 8c):
        // Fixed N+1 query pattern by using Room @Relation
        //
        // BEFORE (N+1 pattern):
        // - 1 query: SELECT * FROM DeckEntity
        // - N queries: SELECT * FROM CardEntity WHERE deckId = ? (one per deck)
        // - Total: 1+N queries (11 queries for 10 decks)
        //
        // AFTER (single JOIN query):
        // - 1 query: SELECT DeckEntity.*, CardEntity.* FROM DeckEntity LEFT JOIN CardEntity...
        // - Total: 1 query (regardless of number of decks)
        //
        // Impact: Instant loading, smooth UI, no database bottleneck
        return dao.getDecksWithCards().map { decksWithCards ->
            decksWithCards.map { deckWithCards ->
                // Convert cards from entity to domain model
                val cards = deckWithCards.cards.map { it.toDomain() }

                // Convert deck from entity to domain model with cards
                deckWithCards.deck.toDomain(cards = cards)
            }
        }
    }

    override suspend fun refreshDecks() {
        // 1. Fetch from network
        val decksFromApi = when (val result = api.fetchAllDecks()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }

        // 2. Get existing decks to preserve progress
        val existingDecks = dao.getDecks().first().associateBy { it.id }

        // 3. Production hardening: write the whole API snapshot through one DAO
        // transaction so stale decks/cards are removed together and saved progress survives.
        val deckEntities = decksFromApi.map { deck ->
            deck.toEntity().copy(
                currentCardIndex = existingDecks[deck.id]?.currentCardIndex ?: 0
            )
        }
        val cardsByDeckId = decksFromApi.associate { deck ->
            deck.id to deck.cards.map { card -> card.toEntity(deckId = deck.id) }
        }

        dao.refreshDecks(deckEntities, cardsByDeckId)

        // 4. Database Flow automatically notifies observers (no return needed)
    }

    override suspend fun updateDeckProgress(deckId: String, cardIndex: Int) {
        dao.updateDeckProgress(deckId, cardIndex)
        // Database Flow automatically notifies observers (reactive update)
    }
}
