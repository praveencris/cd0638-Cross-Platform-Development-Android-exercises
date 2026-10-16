package com.recall.app

/**
 * Android platform implementation
 */
class AndroidPlatform : Platform {
    override val name: String = "Droid"
}

/**
 * Actual implementation of getPlatform() for Android
 */
actual fun getPlatform(): Platform = AndroidPlatform()
