package com.recall.app.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Android implementation of AnalyticsService using Firebase SDK.
 *
 * Solution for Exercise 13a: Firebase Analytics fully implemented.
 * Crashlytics and Remote Config will be added in Exercise 13b.
 */
class AndroidAnalyticsService(context: Context) : AnalyticsService {
    private val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun logEvent(name: String, params: Map<String, Any>?) {
        // Convert Kotlin Map to Android Bundle
        val bundle = params?.let { mapToBundle(it) }
        analytics.logEvent(name, bundle)
    }

    override fun setUserProperty(name: String, value: String) {
        analytics.setUserProperty(name, value)
    }

    override fun recordException(throwable: Throwable) {
        // Stub implementation for Exercise 13a
        // Full implementation coming in Exercise 13b
        println("Analytics: Exception recorded - ${throwable.message}")
    }

    override suspend fun fetchWelcomeMessage(): String {
        // Stub implementation for Exercise 13a
        // Full implementation coming in Exercise 13b
        return "Welcome to Recall!"
    }

    /**
     * Converts a Kotlin Map to an Android Bundle for Firebase Analytics.
     *
     * Firebase Analytics requires Bundle for event parameters, but our common interface
     * uses Map for better Kotlin ergonomics. This helper handles the conversion.
     */
    private fun mapToBundle(map: Map<String, Any>): Bundle {
        val bundle = Bundle()
        map.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Float -> bundle.putFloat(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                else -> bundle.putString(key, value.toString())
            }
        }
        return bundle
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
 * For this solution branch, we'll document the setup requirements in README.
 */
actual object AnalyticsProvider {
    @Volatile
    private var instance: AnalyticsService? = null

    /**
     * Initialize the analytics service with Android Context.
     * Call this once in your Application.onCreate()
     */
    fun initialize(context: Context) {
        if (instance != null) return

        synchronized(this) {
            if (instance == null) {
                instance = AndroidAnalyticsService(context.applicationContext)
            }
        }
    }

    actual fun get(): AnalyticsService {
        return checkNotNull(instance) {
            "AnalyticsProvider not initialized. Call AnalyticsProvider.initialize(context) in Application.onCreate()"
        }
    }
}
