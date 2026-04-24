package com.recall.app.di

import io.ktor.client.HttpClient
import com.recall.app.network.NetworkClient
import com.recall.app.network.RecallApiService
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.onClose
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Shared dependency injection module
 * Provides instances that are common across all platforms
 */
val sharedModule = module {
    // Keep the HTTP client inside DI so it can be disposed with the Koin graph.
    single<HttpClient> { NetworkClient.createHttpClient() } withOptions {
        onClose { client ->
            client?.close()
        }
    }

    // Provide singleton instance of RecallApiService
    single { RecallApiService(get()) }
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
