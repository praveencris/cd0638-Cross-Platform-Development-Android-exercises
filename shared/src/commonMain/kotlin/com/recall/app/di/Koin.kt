package com.recall.app.di

import com.recall.app.MainViewModel
import com.recall.app.network.NetworkClient
import com.recall.app.network.RecallApiService
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Shared dependency injection module
 * Provides instances that are common across all platforms
 */
val sharedModule = module {
    // Provide singleton instance of RecallApiService
    single { RecallApiService(NetworkClient.httpClient) }

    // Provide MainViewModel instance (factory scope - new instance per injection)
    factory { MainViewModel(get()) }
}

/**
 * Initialize Koin dependency injection
 * This function should be called from platform-specific code (Android Application, iOS AppDelegate)
 *
 * @param appDeclaration Platform-specific Koin configuration (e.g., androidContext)
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(sharedModule)
}
