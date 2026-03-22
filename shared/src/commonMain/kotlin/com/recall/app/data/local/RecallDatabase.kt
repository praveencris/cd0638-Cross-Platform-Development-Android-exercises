package com.recall.app.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.recall.app.data.local.dao.RecallDao
import com.recall.app.data.local.entity.CardEntity
import com.recall.app.data.local.entity.DeckEntity

@Database(
    entities = [DeckEntity::class, CardEntity::class],
    version = 1,
    exportSchema = false
)
@ConstructedBy(RecallDatabaseConstructor::class)
abstract class RecallDatabase : RoomDatabase() {
    abstract fun recallDao(): RecallDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object RecallDatabaseConstructor : RoomDatabaseConstructor<RecallDatabase>
