package com.recall.app.di

import org.koin.dsl.module

/**
 * ComposeApp dependency injection module
 * Provides UI-layer dependencies like ViewModels
 *
 * Note: MainViewModel is now registered in the shared module
 * to be available across all platforms (Android, Wear OS, iOS)
 */
val appModule = module {
    // Currently empty - MainViewModel moved to shared module
    // Add platform-specific UI dependencies here if needed
}
