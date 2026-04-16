package com.recall.app.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/**
 * iOS implementation of DatabaseFactory
 * Creates a Room database instance using the app's Documents directory.
 */
actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<RecallDatabase> {
        val documentsDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null
        )?.path ?: error("Unable to resolve the iOS documents directory for the database")
        val dbFilePath = "$documentsDirectory/recall.db"
        return Room.databaseBuilder<RecallDatabase>(
            name = dbFilePath
        )
            .setDriver(BundledSQLiteDriver())
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
    }
}
