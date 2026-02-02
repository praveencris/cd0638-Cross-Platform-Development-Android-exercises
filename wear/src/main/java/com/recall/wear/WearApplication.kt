package com.recall.wear

import android.app.Application
import com.recall.app.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

/**
 * Application class for Wear OS app
 * Initializes Koin dependency injection
 */
class WearApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin with shared module dependencies
        initKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@WearApplication)
        }
    }
}
