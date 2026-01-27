package com.recall.app.network

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

/**
 * Android implementation of HTTP client engine
 * Uses OkHttp engine for Android platform
 */
actual fun createHttpEngine(): HttpClientEngine = OkHttp.create()
