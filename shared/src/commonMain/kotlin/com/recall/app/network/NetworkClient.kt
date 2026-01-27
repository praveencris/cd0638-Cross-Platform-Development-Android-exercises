package com.recall.app.network

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
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

    /**
     * Configured HttpClient instance with JSON support
     * - ContentNegotiation: Handles automatic serialization/deserialization
     * - ignoreUnknownKeys: Allows parsing JSON with extra fields not in our models
     * - Accepts both application/json and text/plain (for GitHub Gist compatibility)
     */
    val httpClient = HttpClient(createHttpEngine()) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            }, contentType = ContentType.Application.Json)

            // Also accept text/plain as JSON (GitHub Gist returns this)
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            }, contentType = ContentType.Text.Plain)
        }
    }
}
