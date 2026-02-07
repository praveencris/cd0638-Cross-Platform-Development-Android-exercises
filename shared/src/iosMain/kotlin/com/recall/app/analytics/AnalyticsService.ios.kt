package com.recall.app.analytics

import cocoapods.FirebaseAnalytics.FIRAnalytics
import cocoapods.FirebaseCrashlytics.FIRCrashlytics
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfig
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfigSettings
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.Foundation.NSNumber
import kotlin.coroutines.resume
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * iOS implementation of AnalyticsService using Firebase SDK via CocoaPods.
 *
 * Exercise 13b: Building on completed Analytics from 13a, add Crashlytics and Remote Config.
 *
 * Note: @OptIn(ExperimentalForeignApi::class) is required for Kotlin/Native cinterop.
 */
@OptIn(ExperimentalForeignApi::class)
class IOSAnalyticsService : AnalyticsService {
    // TODO 4: Initialize FIRRemoteConfig with settings
    // 1. Get remoteConfig instance: FIRRemoteConfig.remoteConfig()
    // 2. Create settings: FIRRemoteConfigSettings()
    // 3. Set minimum fetch interval: settings.setMinimumFetchInterval(3600.0)
    // 4. Apply settings: remoteConfig.setConfigSettings(settings)
    // 5. Set default values: remoteConfig.setDefaults(mapOf("welcome_message" to "Welcome to Recall!"))

    // ✅ Analytics implementation complete from Exercise 13a
    override fun logEvent(name: String, params: Map<String, Any>?) {
        val nsParams = params?.let { mapToNSDictionary(it) }
        FIRAnalytics.logEventWithName(name, nsParams)
    }

    override fun setUserProperty(name: String, value: String) {
        FIRAnalytics.setUserPropertyString(value, name)
    }

    override fun recordException(throwable: Throwable) {
        // TODO 5: Implement recordException() method
        // 1. Convert throwable to NSError using toNSError() extension
        // 2. Call FIRCrashlytics.crashlytics().recordError(error)
    }

    override suspend fun fetchWelcomeMessage(): String {
        // TODO 7: Implement fetchWelcomeMessage() suspend function
        // Use suspendCancellableCoroutine to wrap iOS async callback:
        // 1. Call remoteConfig.fetchAndActivateWithCompletionHandler { status, error -> }
        // 2. Check if error == null
        // 3. Get value: remoteConfig.configValueForKey("welcome_message").stringValue
        // 4. Use ?: "Welcome to Recall!" for fallback
        // 5. Call continuation.resume(message)

        return "Welcome to Recall!" // Temporary placeholder
    }

    // ✅ Type conversion helper complete from Exercise 13a
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

    /**
     * Converts a Kotlin Throwable to an NSError for Crashlytics.
     *
     * Crashlytics on iOS expects NSError objects, so we convert our Kotlin exceptions
     * into the appropriate iOS format.
     */
    private fun Throwable.toNSError(): NSError {
        // TODO 6: Implement Throwable.toNSError() extension
        // Create NSError using NSError.errorWithDomain():
        // - domain: "com.recall.app"
        // - code: 0
        // - userInfo: mapOf<Any?, Any?> with "message" and "stackTrace" keys
        // Use this.message ?: "Unknown error" and this.stackTraceToString()

        return NSError.errorWithDomain(
            domain = "com.recall.app",
            code = 0,
            userInfo = emptyMap<Any?, Any?>()
        ) // Temporary placeholder
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
