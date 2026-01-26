package com.recall.app

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
