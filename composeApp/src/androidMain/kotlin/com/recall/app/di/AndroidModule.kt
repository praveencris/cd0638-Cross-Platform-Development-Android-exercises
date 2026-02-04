package com.recall.app.di

import com.recall.app.data.local.DatabaseFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific dependency injection module
 * Provides Android platform dependencies
 */
val androidModule = module {
    // DatabaseFactory - requires Android Context for Room database file path
    single { DatabaseFactory(androidContext()) }
}
