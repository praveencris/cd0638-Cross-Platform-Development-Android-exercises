package com.recall.app

import androidx.compose.ui.window.ComposeUIViewController
import com.recall.app.di.appModule
import com.recall.app.di.initKoin
import com.recall.app.di.iosModule
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.getKoin

/**
 * iOS entry point for the Recall app
 * Initializes Koin before rendering the shared Compose UI
 */
fun MainViewController() = ComposeUIViewController {
    // Initialize Koin if not already started
    // This is safe for hot reload scenarios
    try {
        getKoin()
    } catch (e: Exception) {
        // Koin not started, initialize it
        initKoin {
            modules(iosModule, appModule)
        }
    }

    App()
}