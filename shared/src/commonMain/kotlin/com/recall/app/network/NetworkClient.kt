package com.recall.app.network

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.*
import kotlinx.serialization.json.Json

/**
 * Platform-specific HTTP client engine
 */
expect fun createHttpEngine(): HttpClientEngine

/**
 * Network client configuration for Ktor HTTP client
 * Configures JSON serialization and content negotiation
 */
object NetworkClient {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * Creates a fresh HttpClient instance with JSON support.
     * Callers own the returned client and must close it when finished.
     *
     * - ContentNegotiation: Handles automatic serialization/deserialization
     * - ignoreUnknownKeys: Allows parsing JSON with extra fields not in our models
     * - Accepts both application/json and text/plain (for GitHub Gist compatibility)
     */
    fun createHttpClient(): HttpClient = HttpClient(createHttpEngine()) {
        install(ContentNegotiation) {
            json(json, contentType = ContentType.Application.Json)

            // Also accept text/plain as JSON (GitHub Gist returns this)
            json(json, contentType = ContentType.Text.Plain)
        }

        // Prevent indefinite hangs on slow or unreachable networks.
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 10_000
        }
    }
}
