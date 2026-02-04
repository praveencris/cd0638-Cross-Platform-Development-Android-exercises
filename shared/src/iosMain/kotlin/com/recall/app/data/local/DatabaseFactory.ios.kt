package com.recall.app.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

/**
 * iOS implementation of DatabaseFactory
 * Creates a Room database instance using NSHomeDirectory for file storage
 */
actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<RecallDatabase> {
        val dbFilePath = NSHomeDirectory() + "/recall.db"
        return Room.databaseBuilder<RecallDatabase>(
            name = dbFilePath
        )
            .setDriver(BundledSQLiteDriver())
            .addMigrations(MIGRATION_1_2)
    }
}
