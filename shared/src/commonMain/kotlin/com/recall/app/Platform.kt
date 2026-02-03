package com.recall.app

import androidx.room.RoomDatabase
import com.recall.app.data.local.RecallDatabase
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Platform interface to identify the current platform
 */
interface Platform {
    val name: String
}

/**
 * Expected platform-specific implementation
 * Each platform (Android, iOS) provides its own actual implementation
 */
expect fun getPlatform(): Platform

/**
 * Expected platform-specific UUID generation
 * Each platform uses its native UUID implementation
 * @return A unique identifier string
 */
expect fun generateUUID(): String

/**
 * Expected platform-specific database builder
 * Each platform (Android, iOS) provides its own Room database configuration
 * @return A configured RoomDatabase.Builder for RecallDatabase
 */
expect fun getDatabaseBuilder(): RoomDatabase.Builder<RecallDatabase>

/**
 * Greeting function that displays platform name and current date
 * @return String containing platform name and today's date
 */
fun greet(): String {
    val currentMoment = Clock.System.now()
    val currentDate = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "Hello, ${getPlatform().name}! Today is $currentDate."
}
