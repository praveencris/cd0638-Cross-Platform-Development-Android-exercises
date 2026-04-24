package com.recall.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.recall.app.data.local.entity.CardEntity
import com.recall.app.data.local.entity.DeckEntity
import com.recall.app.data.local.entity.DeckWithCards
import kotlinx.coroutines.flow.Flow

@Dao
interface RecallDao {
    @Query("SELECT * FROM DeckEntity")
    fun getDecks(): Flow<List<DeckEntity>>

    
    ("SELECT * FROM DeckEntity")
    fun getDecksWithCards(): Flow<List<DeckWithCards>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: DeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardEntity>)

    @Query("DELETE FROM CardEntity WHERE deckId = :deckId")
    suspend fun deleteCardsForDeck(deckId: String)

    @Query("DELETE FROM CardEntity WHERE deckId NOT IN (:deckIds)")
    suspend fun deleteCardsNotInDeckIds(deckIds: List<String>)

    @Query("DELETE FROM DeckEntity WHERE id NOT IN (:deckIds)")
    suspend fun deleteDecksNotIn(deckIds: List<String>)

    @Query("DELETE FROM CardEntity")
    suspend fun deleteAllCards()

    @Query("DELETE FROM DeckEntity")
    suspend fun deleteAllDecks()

    @Query("SELECT * FROM CardEntity WHERE deckId = :deckId")
    suspend fun getCardsForDeck(deckId: String): List<CardEntity>

    @Query("UPDATE DeckEntity SET currentCardIndex = :cardIndex WHERE id = :deckId")
    suspend fun updateDeckProgress(deckId: String, cardIndex: Int)

    @Transaction
    suspend fun refreshDecks(
        decks: List<DeckEntity>,
        cardsByDeckId: Map<String, List<CardEntity>>
    ) {
        val deckIds = decks.map { it.id }
        if (deckIds.isEmpty()) {
            deleteAllCards()
            deleteAllDecks()
            return
        }

        deleteCardsNotInDeckIds(deckIds)
        deleteDecksNotIn(deckIds)

        decks.forEach { deck ->
            insertDeck(deck)
            deleteCardsForDeck(deck.id)
            val cards = cardsByDeckId[deck.id].orEmpty()
            if (cards.isNotEmpty()) {
                insertCards(cards)
            }
        }
    }
}
