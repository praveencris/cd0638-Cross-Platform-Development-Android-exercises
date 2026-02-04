package com.recall.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import com.recall.app.data.local.dao.RecallDao
import com.recall.app.data.local.entity.CardEntity
import com.recall.app.data.local.entity.DeckEntity

/**
 * Migration from version 1 to version 2: Add currentCardIndex column to DeckEntity
 * This enables persistent deck progress tracking across app restarts
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        // Add currentCardIndex column with default value 0 (start from beginning for existing decks)
        connection.prepare("ALTER TABLE DeckEntity ADD COLUMN currentCardIndex INTEGER NOT NULL DEFAULT 0").use {
            it.step()
        }
    }
}

@Database(
    entities = [DeckEntity::class, CardEntity::class],
    version = 2,  // Bumped from 1 to 2 for progress tracking feature
    exportSchema = true
)
abstract class RecallDatabase : RoomDatabase() {
    abstract fun recallDao(): RecallDao
}
