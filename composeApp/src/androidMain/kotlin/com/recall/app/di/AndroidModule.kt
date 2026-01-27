package com.recall.app.di

import com.recall.app.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Android-specific dependency injection module
 * Provides Android platform dependencies like ViewModels
 */
val androidModule = module {
    // Provide MainViewModel with injected RecallApiService from shared module
    viewModel { MainViewModel(get()) }
}
