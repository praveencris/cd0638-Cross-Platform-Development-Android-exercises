package com.recall.app.network

import io.ktor.client.*
import io.ktor.client.engine.*

/**
 * Platform-specific HTTP client engine
 */
expect fun createHttpEngine(): HttpClientEngine

/**
 * Network client configuration for Ktor HTTP client
 * Configures JSON serialization and content negotiation
 */
object NetworkClient {

    /**
     * Configured HttpClient instance with JSON support
     * - ContentNegotiation: Handles automatic serialization/deserialization
     * - ignoreUnknownKeys: Allows parsing JSON with extra fields not in our models
     * - Accepts both application/json and text/plain (for GitHub Gist compatibility)
     */
    val httpClient = HttpClient(createHttpEngine()) {
        // TODO: Install ContentNegotiation and configure JSON
    }
}
