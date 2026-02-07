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
 * Exercise 13b: Building on completed Analytics from 13a, add Crashlytics and Remote Config.
 */
class AndroidAnalyticsService(context: Context) : AnalyticsService {
    // ✅ Analytics complete from Exercise 13a
    private val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    // TODO 1: Initialize Firebase Crashlytics and Remote Config
    // 1. Initialize crashlytics: FirebaseCrashlytics.getInstance()
    // 2. Initialize remoteConfig: FirebaseRemoteConfig.getInstance()
    // 3. Set Remote Config fetch interval to 3600 seconds using:
    //    - FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build()
    //    - remoteConfig.setConfigSettingsAsync(settings)
    // 4. Set default values: remoteConfig.setDefaultsAsync(mapOf("welcome_message" to "Welcome to Recall!"))

    // ✅ Analytics implementation complete from Exercise 13a
    override fun logEvent(name: String, params: Map<String, Any>?) {
        val bundle = params?.let { mapToBundle(it) }
        analytics.logEvent(name, bundle)
    }

    override fun setUserProperty(name: String, value: String) {
        analytics.setUserProperty(name, value)
    }

    override fun recordException(throwable: Throwable) {
        // TODO 2: Implement recordException() method
        // One line: crashlytics.recordException(throwable)
    }

    override suspend fun fetchWelcomeMessage(): String {
        // TODO 3: Implement fetchWelcomeMessage() suspend function
        // Use suspendCancellableCoroutine to wrap Firebase's async callback:
        // 1. Call remoteConfig.fetchAndActivate()
        // 2. Add OnCompleteListener { task ->
        // 3. If task.isSuccessful, get value: remoteConfig.getString("welcome_message")
        // 4. Otherwise, return default: "Welcome to Recall!"
        // 5. Resume continuation with result: continuation.resume(message)

        return "Welcome to Recall!" // Temporary placeholder
    }

    // ✅ Type conversion helper complete from Exercise 13a
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
