package com.recall.app.di

import com.recall.app.data.local.DatabaseFactory
import org.koin.dsl.module

/**
 * iOS-specific dependency injection module
 * Provides iOS platform dependencies
 */
val iosModule = module {
    // DatabaseFactory - no context needed for iOS (uses NSHomeDirectory)
    single { DatabaseFactory() }
}
