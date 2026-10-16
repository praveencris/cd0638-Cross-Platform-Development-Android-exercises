package com.recall.app

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
 * Greeting function that displays platform name and current date
 * @return String containing platform name and today's date
 */
fun greet(): String {
    // TODO-DONE: Use kotlinx-datetime to add the current date
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "Hello, ${getPlatform().name}! Date: $today"
}
