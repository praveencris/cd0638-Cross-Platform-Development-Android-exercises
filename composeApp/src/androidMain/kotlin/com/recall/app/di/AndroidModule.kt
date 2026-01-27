package com.recall.app.di

import org.koin.dsl.module

/**
 * Android-specific dependency injection module
 * Provides Android platform dependencies
 */
val androidModule = module {
    // Android-specific dependencies can be added here
    // MainViewModel is now registered in the shared module
}
