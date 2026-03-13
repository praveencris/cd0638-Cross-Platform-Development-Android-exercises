package com.recall.wear.datalayer

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

/**
 * Listens for Data Layer events from connected devices.
 *
 * This service runs in the background and is automatically started by
 * Google Play Services when relevant Data Layer events occur.
 *
 * It handles two types of events:
 * 1. Data changes: When a DataItem at "/recall/deck_summary" is updated
 * 2. Messages: When a message is received (e.g., "/recall/sync_complete")
 *
 * For this demo, we log the events to Logcat. In a production app, you would
 * update local storage, trigger UI refreshes, or show notifications.
 */
class DataLayerListenerService : WearableListenerService() {

    companion object {
        private const val TAG = "DataLayerListener"
    }

    /**
     * Called when DataItems are created or modified on a connected device.
     *
     * We filter for our specific path "/recall/deck_summary" and extract
     * the deck name and card count from the DataMap.
     */
    override fun onDataChanged(events: DataEventBuffer) {
        events.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path == "/recall/deck_summary") {
                    val dataMap = DataMapItem
                        .fromDataItem(event.dataItem).dataMap
                    val name = dataMap.getString("deck_name")
                    val count = dataMap.getInt("card_count")
                    Log.d(TAG, "Received deck summary: $name, cards: $count")
                }
            }
        }
    }

    /**
     * Called when a message is received from a connected device.
     *
     * Messages are one-off communications (not persisted like DataItems).
     * We log the message path for demonstration purposes.
     */
    override fun onMessageReceived(event: MessageEvent) {
        Log.d(TAG, "Message received: ${event.path}")
    }
}
