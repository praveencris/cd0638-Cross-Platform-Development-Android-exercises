package com.recall.app.data.local

import androidx.room.RoomDatabase

/**
 * Platform-specific factory for creating Room database instances
 * Uses expect/actual pattern to provide platform-specific implementations
 */
expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<RecallDatabase>
}
