package com.recall.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.recall.app.data.local.dao.RecallDao
import com.recall.app.data.local.entity.CardEntity
import com.recall.app.data.local.entity.DeckEntity

@Database(
    entities = [DeckEntity::class, CardEntity::class],
    version = 1,
    exportSchema = true
)
abstract class RecallDatabase : RoomDatabase() {
    abstract fun recallDao(): RecallDao
}
