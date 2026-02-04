package com.recall.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Android implementation of DatabaseFactory
 * Creates a Room database instance using the application context
 */
actual class DatabaseFactory(private val context: Context) {
    actual fun create(): RoomDatabase.Builder<RecallDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath("recall.db")
        return Room.databaseBuilder<RecallDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}
