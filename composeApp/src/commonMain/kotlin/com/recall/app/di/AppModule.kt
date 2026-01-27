package com.recall.app.di

import com.recall.app.MainViewModel
import org.koin.dsl.module

/**
 * ComposeApp dependency injection module
 * Provides UI-layer dependencies like ViewModels
 */
val appModule = module {
    // Provide factory instance of MainViewModel
    factory { MainViewModel(get()) }
}
