package com.recall.wear.di

import com.recall.wear.data.DeckSnapshotStore
import com.recall.wear.data.HealthServicesManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val wearModule = module {
    single { DeckSnapshotStore(androidContext()) }
    single { HealthServicesManager(androidContext()) }
}
