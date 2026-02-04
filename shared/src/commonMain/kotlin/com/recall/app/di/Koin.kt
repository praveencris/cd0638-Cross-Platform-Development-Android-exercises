package com.recall.app.di

import com.recall.app.MainViewModel
import com.recall.app.data.RecallRepository
import com.recall.app.data.RecallRepositoryImpl
import com.recall.app.data.local.DatabaseFactory
import com.recall.app.data.local.RecallDatabase
import com.recall.app.network.NetworkClient
import com.recall.app.network.RecallApiService
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Shared dependency injection module
 * Provides instances that are common across all platforms
 *
 * Architecture layers (bottom to top):
 * 1. Database Layer: DatabaseFactory, RecallDatabase, RecallDao
 * 2. Network Layer: RecallApiService
 * 3. Repository Layer: RecallRepository (Single Source of Truth)
 * 4. ViewModel Layer: MainViewModel
 */
val sharedModule = module {
    // ==================== Database Layer ====================
    // Note: DatabaseFactory must be provided by platform-specific code
    // Android: Provides Context, iOS: No parameters needed

    // RecallDatabase - Room database instance
    single<RecallDatabase> {
        get<DatabaseFactory>()
            .create()
            .build()
    }

    // RecallDao - Data Access Object from database
    single { get<RecallDatabase>().recallDao() }

    // ==================== Network Layer ====================
    // RecallApiService - HTTP client for remote API
    single { RecallApiService(NetworkClient.httpClient) }

    // ==================== Repository Layer ====================
    // RecallRepository - Mediates between network and database (Single Source of Truth)
    single<RecallRepository> { RecallRepositoryImpl(api = get(), dao = get()) }

    // ==================== ViewModel Layer ====================
    // MainViewModel - Observes repository and manages UI state
    // Now injects RecallRepository instead of RecallApiService
    factory { MainViewModel(repository = get()) }
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
