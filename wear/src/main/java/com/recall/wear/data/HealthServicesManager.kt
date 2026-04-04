package com.recall.wear.data

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

/**
 * Manager for Health Services integration
 * Provides access to heart rate data from Wear OS sensors
 */
class HealthServicesManager(context: Context) {

    private val measureClient = HealthServices.getClient(context).measureClient

    /**
     * Flow of heart rate values (BPM)
     * Returns null when sensor is unavailable or acquiring
     * Automatically unregisters when Flow is cancelled (lifecycle-aware)
     */
    fun heartRateFlow(): Flow<Int?> = callbackFlow {
        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(
                dataType: DeltaDataType<*, *>,
                availability: Availability
            ) {
                // When sensor is unavailable or acquiring, emit null
                if (availability !is DataTypeAvailability) {
                    trySend(null)
                }
            }

            override fun onDataReceived(data: DataPointContainer) {
                // Extract heart rate value from data points
                val heartRatePoints = data.getData(DataType.HEART_RATE_BPM)
                val latestHeartRate = heartRatePoints.lastOrNull()?.value
                trySend(latestHeartRate?.toInt())
            }
        }

        // Register for heart rate updates
        measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, callback)

        // Unregister when Flow is cancelled (e.g., when composable leaves composition)
        // Non-blocking cleanup to avoid potential deadlock on single-threaded dispatchers
        awaitClose {
            kotlinx.coroutines.GlobalScope.launch {
                measureClient.unregisterMeasureCallbackAsync(DataType.HEART_RATE_BPM, callback)
            }
        }
    }

    /**
     * Check if heart rate monitoring is supported on this device
     */
    suspend fun isHeartRateAvailable(): Boolean {
        val capabilities = measureClient.getCapabilitiesAsync().await()
        return DataType.HEART_RATE_BPM in capabilities.supportedDataTypesMeasure
    }
}
