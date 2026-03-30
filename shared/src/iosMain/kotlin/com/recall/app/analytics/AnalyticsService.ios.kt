package com.recall.app.analytics

import cocoapods.FirebaseAnalytics.FIRAnalytics
import cocoapods.FirebaseCore.FIRApp
import platform.Foundation.NSNumber
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * iOS implementation of AnalyticsService using Firebase SDK via CocoaPods.
 *
 * Solution for Exercise 13a: Firebase Analytics fully implemented.
 * Crashlytics and Remote Config will be added in Exercise 13b.
 *
 * Note: @OptIn(ExperimentalForeignApi::class) is required for Kotlin/Native cinterop.
 */
@OptIn(ExperimentalForeignApi::class)
class IOSAnalyticsService : AnalyticsService {
    override fun logEvent(name: String, params: Map<String, Any>?) {
        // Convert Kotlin Map to NSDictionary for Firebase
        val nsParams = params?.let { mapToNSDictionary(it) }
        FIRAnalytics.logEventWithName(name, nsParams)
    }

    override fun setUserProperty(name: String, value: String) {
        FIRAnalytics.setUserPropertyString(value, name)
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
     * Converts a Kotlin Map to an NSDictionary for Firebase iOS API.
     *
     * Firebase iOS uses NSDictionary for event parameters, so we need to convert
     * from our Kotlin Map.
     */
    private fun mapToNSDictionary(map: Map<String, Any>): Map<Any?, *> {
        return map.mapValues { (_, value) ->
            when (value) {
                is String -> value
                is Int -> NSNumber(int = value)
                is Long -> NSNumber(longLong = value)
                is Double -> NSNumber(double = value)
                is Float -> NSNumber(float = value)
                is Boolean -> NSNumber(bool = value)
                else -> value.toString()
            }
        }
    }
}

/**
 * iOS actual implementation of AnalyticsProvider.
 *
 * Firebase is initialized here via FIRApp.configure() before creating the service.
 * Unlike Android (where the google-services Gradle plugin auto-initializes Firebase),
 * iOS requires an explicit configure() call before any Firebase API usage.
 *
 * Note: @OptIn(ExperimentalForeignApi::class) is required for Kotlin/Native cinterop.
 */
@OptIn(ExperimentalForeignApi::class)
actual object AnalyticsProvider {
    private val instance: AnalyticsService by lazy {
        // Initialize Firebase before first use
        if (FIRApp.defaultApp() == null) {
            FIRApp.configure()
            println("Firebase initialized on iOS")
        }
        IOSAnalyticsService()
    }

    actual fun get(): AnalyticsService = instance
}
