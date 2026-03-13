package com.recall.wear

import android.app.Application
import com.recall.app.di.initKoin
import com.recall.wear.datalayer.WearDataLayerService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.koin.dsl.module

/**
 * Application class for Wear OS app
 * Initializes Koin dependency injection with both shared and wear-specific modules
 */
class WearApplication : Application() {

    /**
     * Wear-specific Koin module
     * Provides dependencies that only exist in the wear module
     */
    private val wearModule = module {
        single { WearDataLayerService(androidContext()) }
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin with shared module dependencies + wear-specific module
        initKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@WearApplication)
            modules(wearModule)
        }
    }
}
