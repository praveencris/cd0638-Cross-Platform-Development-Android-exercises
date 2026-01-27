package com.recall.app

import android.app.Application

/**
 * Application class for Recall app
 * Initializes Koin dependency injection with Android context
 */
class RecallApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // TODO: Initialize Koin with Android Context
        // Hint: Call initKoin() from shared module and pass androidContext(this@RecallApp)
    }
}
