package com.recall.app.analytics

import cocoapods.FirebaseAnalytics.FIRAnalytics
import platform.Foundation.NSNumber
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * iOS implementation of AnalyticsService using Firebase SDK via CocoaPods.
 *
 * Exercise 13a: This implementation focuses on Firebase Analytics only.
 * Crashlytics and Remote Config will be added in Exercise 13b.
 *
 * Note: @OptIn(ExperimentalForeignApi::class) is required for Kotlin/Native cinterop.
 */
@OptIn(ExperimentalForeignApi::class)
class IOSAnalyticsService : AnalyticsService {
    override fun logEvent(name: String, params: Map<String, Any>?) {
        // TODO 5: Implement logEvent method
        // 1. Convert params Map to NSDictionary using mapToNSDictionary() helper
        // 2. Call FIRAnalytics.logEventWithName(name, nsParams)
        // Hint: Handle null params with params?.let { }
    }

    override fun setUserProperty(name: String, value: String) {
        // TODO 6: Implement setUserProperty method
        // One line: FIRAnalytics.setUserPropertyString(value, name)
        // Note: iOS API has reversed parameter order (value first, then name)
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
     * Converts a Kotlin Map to an NSDictionary for Firebase iOS API.
     *
     * Firebase iOS uses NSDictionary for event parameters, so we need to convert
     * from our Kotlin Map.
     */
    private fun mapToNSDictionary(map: Map<String, Any>): Map<Any?, *> {
        // TODO 7: Implement mapToNSDictionary helper
        // Use map.mapValues { (_, value) -> to convert each value:
        // - String: keep as-is
        // - Int: NSNumber(int = value)
        // - Long: NSNumber(longLong = value)
        // - Double: NSNumber(double = value)
        // - Float: NSNumber(float = value)
        // - Boolean: NSNumber(bool = value)
        // - else: value.toString()

        return emptyMap<Any?, Any?>() // Temporary placeholder
    }
}

/**
 * iOS actual implementation of AnalyticsProvider.
 *
 * Firebase is initialized in the iOS app's AppDelegate (iosApp/iosApp/iOSApp.swift)
 * by calling FirebaseApp.configure(), so we can create the service instance directly.
 *
 * Note: @OptIn(ExperimentalForeignApi::class) is required for Kotlin/Native cinterop.
 */
@OptIn(ExperimentalForeignApi::class)
actual object AnalyticsProvider {
    private val instance: AnalyticsService by lazy {
        IOSAnalyticsService()
    }

    actual fun get(): AnalyticsService = instance
}
