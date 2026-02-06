package com.recall.app.analytics

/**
 * Cross-platform analytics service interface.
 *
 * This interface provides a lightweight abstraction over Firebase Analytics, Crashlytics,
 * and Remote Config. By using expect/actual, we avoid heavyweight multiplatform libraries
 * and only bridge the specific methods we need.
 */
interface AnalyticsService {
    /**
     * Logs a custom event with optional parameters.
     *
     * @param name The event name (e.g., "deck_created", "study_session_completed")
     * @param params Optional key-value pairs for event parameters
     */
    fun logEvent(name: String, params: Map<String, Any>? = null)

    /**
     * Sets a user property that persists across sessions.
     *
     * @param name The property name (e.g., "deck_count", "user_tier")
     * @param value The property value
     */
    fun setUserProperty(name: String, value: String)

    /**
     * Records a non-fatal exception for crash reporting.
     *
     * @param throwable The exception to record
     */
    fun recordException(throwable: Throwable)

    /**
     * Fetches the welcome message from Remote Config.
     *
     * This demonstrates the power of Remote Config - we can change the app's
     * welcome message without releasing a new version to the store.
     *
     * @return The welcome message (defaults to local fallback if fetch fails)
     */
    suspend fun fetchWelcomeMessage(): String
}

/**
 * Platform-specific provider for AnalyticsService.
 *
 * Each platform (Android, iOS) provides its own implementation using expect/actual.
 */
expect object AnalyticsProvider {
    fun get(): AnalyticsService
}
