package com.recall.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.recall.app.data.local.entity.CardEntity
import com.recall.app.data.local.entity.DeckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecallDao {
    @Query("SELECT * FROM DeckEntity")
    fun getDecks(): Flow<List<DeckEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: DeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardEntity>)

    @Query("SELECT * FROM CardEntity WHERE deckId = :deckId")
    suspend fun getCardsForDeck(deckId: String): List<CardEntity>

    @Query("UPDATE DeckEntity SET currentCardIndex = :cardIndex WHERE id = :deckId")
    suspend fun updateDeckProgress(deckId: String, cardIndex: Int)
}
