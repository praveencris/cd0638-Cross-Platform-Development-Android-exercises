package com.recall.app.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Android implementation of AnalyticsService using Firebase SDK.
 *
 * This class wraps Firebase Analytics, Crashlytics, and Remote Config to provide
 * a clean Kotlin API that works seamlessly with KMP architecture.
 */
class AndroidAnalyticsService(context: Context) : AnalyticsService {
    private val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance().apply {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // Fetch at most once per hour
            .build()
        setConfigSettingsAsync(configSettings)

        // Set default values
        setDefaultsAsync(mapOf("welcome_message" to "Welcome to Recall!"))
    }

    override fun logEvent(name: String, params: Map<String, Any>?) {
        // Convert Kotlin Map to Android Bundle
        val bundle = params?.let { mapToBundle(it) }
        analytics.logEvent(name, bundle)
    }

    override fun setUserProperty(name: String, value: String) {
        analytics.setUserProperty(name, value)
    }

    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    override suspend fun fetchWelcomeMessage(): String = suspendCancellableCoroutine { continuation ->
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                val message = if (task.isSuccessful) {
                    remoteConfig.getString("welcome_message")
                } else {
                    // Fallback to default if fetch fails
                    "Welcome to Recall!"
                }
                continuation.resume(message)
            }
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
