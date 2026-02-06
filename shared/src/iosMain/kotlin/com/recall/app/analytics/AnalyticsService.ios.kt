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
 * This implementation uses Kotlin/Native cinterop to call the native Objective-C
 * Firebase SDK. The Podfile includes Firebase/Analytics, Firebase/Crashlytics,
 * and Firebase/RemoteConfig.
 */
@OptIn(ExperimentalForeignApi::class)
class IOSAnalyticsService : AnalyticsService {
    private val remoteConfig: FIRRemoteConfig = FIRRemoteConfig.remoteConfig().apply {
        val settings = FIRRemoteConfigSettings().apply {
            setMinimumFetchInterval(3600.0) // Fetch at most once per hour
        }
        setConfigSettings(settings)

        // Set default values
        setDefaults(mapOf("welcome_message" to "Welcome to Recall!"))
    }

    override fun logEvent(name: String, params: Map<String, Any>?) {
        // Convert Kotlin Map to NSDictionary for Firebase
        val nsParams = params?.let { mapToNSDictionary(it) }
        FIRAnalytics.logEventWithName(name, nsParams)
    }

    override fun setUserProperty(name: String, value: String) {
        FIRAnalytics.setUserPropertyString(value, name)
    }

    override fun recordException(throwable: Throwable) {
        // Convert Kotlin Throwable to NSError
        val error = throwable.toNSError()
        FIRCrashlytics.crashlytics().recordError(error)
    }

    override suspend fun fetchWelcomeMessage(): String = suspendCancellableCoroutine { continuation ->
        remoteConfig.fetchAndActivateWithCompletionHandler { status, error ->
            val message = if (error == null) {
                remoteConfig.configValueForKey("welcome_message").stringValue ?: "Welcome to Recall!"
            } else {
                // Fallback to default if fetch fails
                "Welcome to Recall!"
            }
            continuation.resume(message)
        }
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

    /**
     * Converts a Kotlin Throwable to an NSError for Crashlytics.
     *
     * Crashlytics on iOS expects NSError objects, so we convert our Kotlin exceptions
     * into the appropriate iOS format.
     */
    private fun Throwable.toNSError(): NSError {
        return NSError.errorWithDomain(
            domain = "com.recall.app",
            code = 0,
            userInfo = mapOf(
                "message" to (this.message ?: "Unknown error"),
                "stackTrace" to (this.stackTraceToString())
            )
        )
    }
}

/**
 * iOS actual implementation of AnalyticsProvider.
 *
 * Firebase is initialized in the iOS app's AppDelegate (iosApp/iosApp/iOSApp.swift)
 * by calling FirebaseApp.configure(), so we can create the service instance directly.
 */
@OptIn(ExperimentalForeignApi::class)
actual object AnalyticsProvider {
    private val instance: AnalyticsService by lazy {
        IOSAnalyticsService()
    }

    actual fun get(): AnalyticsService = instance
}
