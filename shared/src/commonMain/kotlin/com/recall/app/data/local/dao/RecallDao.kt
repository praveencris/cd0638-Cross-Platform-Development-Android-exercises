package com.recall.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.recall.app.data.local.entity.CardEntity
import com.recall.app.data.local.entity.DeckEntity
import com.recall.app.data.local.entity.DeckWithCards
import kotlinx.coroutines.flow.Flow

@Dao
interface RecallDao {
    @Query("SELECT * FROM DeckEntity")
    fun getDecks(): Flow<List<DeckEntity>>

    // TODO: Add a @Transaction query that returns Flow<List<DeckWithCards>>
    // This replaces the N+1 pattern where getDecks() + getCardsForDeck() runs per deck
    // Hint: Use @Transaction + @Query("SELECT * FROM DeckEntity") with DeckWithCards return type
    // Remember to import: androidx.room.Transaction and DeckWithCards

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: DeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardEntity>)

    @Query("SELECT * FROM CardEntity WHERE deckId = :deckId")
    suspend fun getCardsForDeck(deckId: String): List<CardEntity>

    @Query("UPDATE DeckEntity SET currentCardIndex = :cardIndex WHERE id = :deckId")
    suspend fun updateDeckProgress(deckId: String, cardIndex: Int)
}
