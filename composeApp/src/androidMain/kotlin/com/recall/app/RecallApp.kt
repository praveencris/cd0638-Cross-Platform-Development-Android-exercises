package com.recall.app

import android.app.Application
import com.recall.app.di.androidModule
import com.recall.app.di.appModule
import com.recall.app.di.initKoin
import org.koin.android.ext.koin.androidContext

/**
 * Application class for Recall app
 * Initializes Koin dependency injection with Android context
 */
class RecallApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin DI framework
        // The initKoin() function comes from shared module
        // We provide Android context and platform-specific modules
        initKoin {
            androidContext(this@RecallApp)
            modules(androidModule, appModule)
        }
    }
}
