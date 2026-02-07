package com.recall.app.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Android implementation of AnalyticsService using Firebase SDK.
 *
 * Exercise 13a: This implementation focuses on Firebase Analytics only.
 * Crashlytics and Remote Config will be added in Exercise 13b.
 */
class AndroidAnalyticsService(context: Context) : AnalyticsService {
    // TODO 1: Initialize FirebaseAnalytics
    // Use FirebaseAnalytics.getInstance(context)
    private val analytics: FirebaseAnalytics = TODO("Initialize FirebaseAnalytics")

    override fun logEvent(name: String, params: Map<String, Any>?) {
        // TODO 2: Implement logEvent method
        // 1. Convert params Map to Bundle using mapToBundle() helper
        // 2. Call analytics.logEvent(name, bundle)
        // Hint: Handle null params with params?.let { }
    }

    override fun setUserProperty(name: String, value: String) {
        // TODO 3: Implement setUserProperty method
        // One line: analytics.setUserProperty(name, value)
    }

    override fun recordException(throwable: Throwable) {
        // Stub implementation for Exercise 13a
        // This will be implemented in Exercise 13b using Firebase Crashlytics
        println("Analytics: Exception recorded - ${throwable.message}")
    }

    override suspend fun fetchWelcomeMessage(): String {
        // Stub implementation for Exercise 13a
        // This will be implemented in Exercise 13b using Firebase Remote Config
        return "Welcome to Recall!"
    }

    /**
     * Converts a Kotlin Map to an Android Bundle for Firebase Analytics.
     *
     * Firebase Analytics requires Bundle for event parameters, but our common interface
     * uses Map for better Kotlin ergonomics. This helper handles the conversion.
     */
    private fun mapToBundle(map: Map<String, Any>): Bundle {
        // TODO 4: Implement mapToBundle helper
        // 1. Create a Bundle
        // 2. Iterate through the map with map.forEach { (key, value) ->
        // 3. Use when (value) to check the type
        // 4. Call the appropriate bundle.putXxx() method
        // Handle these types: String, Int, Long, Double, Float, Boolean
        // For unknown types, use value.toString()

        return Bundle() // Temporary placeholder
    }
}

/**
 * Android actual implementation of AnalyticsProvider.
 *
 * NOTE: This requires Android Context for initialization. In a real app, you would:
 * 1. Initialize this in your Application class
 * 2. Store the instance in a dependency injection container (e.g., Koin)
 * 3. Inject it wherever analytics are needed
 *
 * For this exercise, we'll document the setup requirements in README.
 */
actual object AnalyticsProvider {
    private lateinit var instance: AnalyticsService

    /**
     * Initialize the analytics service with Android Context.
     * Call this once in your Application.onCreate()
     */
    fun initialize(context: Context) {
        instance = AndroidAnalyticsService(context.applicationContext)
    }

    actual fun get(): AnalyticsService {
        check(::instance.isInitialized) {
            "AnalyticsProvider not initialized. Call AnalyticsProvider.initialize(context) in Application.onCreate()"
        }
        return instance
    }
}
