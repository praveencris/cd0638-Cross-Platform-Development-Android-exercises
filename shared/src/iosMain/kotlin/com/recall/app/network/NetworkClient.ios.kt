package com.recall.app.network

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

/**
 * iOS implementation of HTTP client engine
 * Uses Darwin engine for iOS platform
 */
actual fun createHttpEngine(): HttpClientEngine = Darwin.create()
