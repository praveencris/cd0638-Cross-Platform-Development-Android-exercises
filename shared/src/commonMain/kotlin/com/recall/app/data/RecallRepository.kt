package com.recall.app.data

import com.recall.app.data.local.dao.RecallDao
import com.recall.app.data.local.toDomain
import com.recall.app.data.local.toEntity
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
 * Implementation of RecallRepository
 *
 * EXERCISE STARTER CODE:
 * This repository currently uses the API directly. Your task is to refactor it
 * to implement the Offline-First pattern by using the database as the Single Source of Truth.
 */
class RecallRepositoryImpl(
    private val api: RecallApiService
    // TODO 4: Inject RecallDao into the constructor
    // Hint: Add a parameter: private val dao: RecallDao
    // Remember to update the Koin registration in Koin.kt after adding this!
) : RecallRepository {
    override fun getDecks(): Flow<List<Deck>> {
        // TODO 5: Refactor to return data from database (Single Source of Truth)
        // Current: Returns API data directly (not offline-first)
        // Goal: return dao.getDecks().map { deckEntities -> ... }
        //
        // Steps:
        // 1. Call dao.getDecks() to get Flow<List<DeckEntity>>
        // 2. Use .map{} to transform each DeckEntity to Deck domain model
        // 3. For each deck, fetch cards using dao.getCardsForDeck(deckId)
        // 4. Use toDomain() extension functions to convert entities to domain models
        //
        // Once implemented, the UI will:
        // - Show cached data instantly (no loading spinner)
        // - Update automatically when database changes
        // - Work offline with cached data

        // TEMPORARY: Direct API call (remove this after implementing TODO 5)
        return kotlinx.coroutines.flow.flow {
            emit(api.fetchAllDecks())
        }
    }

    override suspend fun refreshDecks() {
        // TODO 6: Write fetched data to database instead of just fetching
        // Current: Only fetches from API (data not persisted)
        // Goal: Fetch from API, then save to database
        //
        // Steps:
        // 1. Fetch decks from API using Result pattern:
        //    when (val result = api.fetchAllDecks()) {
        //        is Result.Success -> { /* use result.data */ }
        //        is Result.Error -> throw result.exception
        //    }
        // 2. For each deck:
        //    a. Convert to DeckEntity using deck.toEntity()
        //    b. Save deck using dao.insertDeck(deckEntity)
        //    c. Convert cards using card.toEntity(deckId)
        //    d. Save cards using dao.insertCards(cardEntities)
        // 3. Database Flow will automatically notify observers (no return needed)
        //
        // BONUS: Preserve existing progress when syncing
        // - Before step 2, fetch existing decks: dao.getDecks().first()
        // - When creating deckEntity, copy currentCardIndex from existing deck
        //
        // Once implemented:
        // - Pull-to-refresh will update database
        // - UI updates automatically via database Flow
        // - Data persists across app restarts

        // TEMPORARY: Just fetch without saving (remove this after implementing TODO 6)
        api.fetchAllDecks()
    }

    override suspend fun updateDeckProgress(deckId: String, cardIndex: Int) {
        // ✅ PRE-IMPLEMENTED: This method is complete
        // It will work once you complete TODO 4 (inject dao)
        TODO("Complete TODO 4 first: Inject dao into constructor, then uncomment below")
        // dao.updateDeckProgress(deckId, cardIndex)
        // Database Flow automatically notifies observers (reactive update)
    }
}
