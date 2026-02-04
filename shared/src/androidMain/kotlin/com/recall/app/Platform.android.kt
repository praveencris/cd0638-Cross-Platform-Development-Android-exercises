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

/**
 * Actual implementation of date formatting for Android
 * Uses java.time API with locale-aware SHORT format
 * Respects system locale (e.g., MM/DD/YY for US, DD/MM/YY for Europe)
 */
actual fun formatShortDate(epochMillis: Long): String {
    val instant = java.time.Instant.ofEpochMilli(epochMillis)
    val formatter = java.time.format.DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.SHORT)
        .withLocale(java.util.Locale.getDefault())
        .withZone(java.time.ZoneId.systemDefault())
    return formatter.format(instant)
}
