package com.recall.app.di

import com.recall.app.data.local.DatabaseFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific dependency injection module
 * Provides Android platform dependencies
 */
val androidModule = module {
    // TODO 1: Register DatabaseFactory as a single definition
    // Hint: DatabaseFactory needs Android Context to create the Room database file path
    // Use: single { DatabaseFactory(androidContext()) }
}
