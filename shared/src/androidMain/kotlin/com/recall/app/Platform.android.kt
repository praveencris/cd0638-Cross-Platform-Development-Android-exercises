package com.recall.app

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.recall.app.data.local.RecallDatabase

/**
 * Android platform implementation
 */
class AndroidPlatform : Platform {
    override val name: String = "Android"
}

/**
 * Actual implementation of getPlatform() for Android
 */
actual fun getPlatform(): Platform = AndroidPlatform()

/**
 * Actual implementation of UUID generation for Android
 * Uses java.util.UUID for native UUID generation
 */
actual fun generateUUID(): String = java.util.UUID.randomUUID().toString()

/**
 * Application context holder for database initialization
 * Must be initialized in Application.onCreate() via initializeDatabase()
 */
private lateinit var applicationContext: Context

/**
 * Initialize the database context
 * Call this from your Application class onCreate()
 */
fun initializeDatabase(context: Context) {
    applicationContext = context.applicationContext
}

/**
 * Actual implementation of database builder for Android
 * Creates a Room database in the app's internal storage
 */
actual fun getDatabaseBuilder(): RoomDatabase.Builder<RecallDatabase> {
    val context = applicationContext
    return Room.databaseBuilder<RecallDatabase>(
        context = context,
        name = "recall.db"
    )
}
