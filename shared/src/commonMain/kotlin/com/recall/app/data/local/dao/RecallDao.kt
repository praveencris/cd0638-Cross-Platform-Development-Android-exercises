package com.recall.app.data.local.dao

import com.recall.app.data.local.entity.CardEntity
import com.recall.app.data.local.entity.DeckEntity
import kotlinx.coroutines.flow.Flow

// TODO 2: Import and add @Dao annotation
//  Hint: import androidx.room.Dao

// TODO 2: Annotate this interface with @Dao
interface RecallDao {

    // TODO 2: Implement getDecks() method
    //  - Add @Query annotation with SQL: "SELECT * FROM DeckEntity"
    //  - Return type: Flow<List<DeckEntity>>
    //  - This returns a reactive Flow that emits updates when data changes
    //  Hint: import androidx.room.Query
    //  Hint: Method signature:
    //    @Query("SELECT * FROM DeckEntity")
    //    fun getDecks(): Flow<List<DeckEntity>>

    // TODO 3: Implement insertDeck() method
    //  - Add @Insert annotation with onConflict = OnConflictStrategy.REPLACE
    //  - Make it a suspend function (for coroutines)
    //  - Parameter: deck: DeckEntity
    //  - Return type: Unit (no return value)
    //  Hint: import androidx.room.Insert
    //  Hint: import androidx.room.OnConflictStrategy
    //  Hint: Method signature:
    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //    suspend fun insertDeck(deck: DeckEntity)

    // TODO 3: Implement insertCards() method
    //  - Add @Insert annotation with onConflict = OnConflictStrategy.REPLACE
    //  - Make it a suspend function
    //  - Parameter: cards: List<CardEntity>
    //  - This allows batch insertion of multiple cards
    //  Hint: Method signature:
    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //    suspend fun insertCards(cards: List<CardEntity>)

    // TODO 3: Implement getCardsForDeck() method
    //  - Add @Query annotation with SQL: "SELECT * FROM CardEntity WHERE deckId = :deckId"
    //  - Make it a suspend function
    //  - Parameter: deckId: String
    //  - Return type: List<CardEntity>
    //  Note: This query will be used in a future lesson to demonstrate
    //        performance profiling (intentionally not using Flow here)
    //  Hint: Method signature:
    //    @Query("SELECT * FROM CardEntity WHERE deckId = :deckId")
    //    suspend fun getCardsForDeck(deckId: String): List<CardEntity>
}
