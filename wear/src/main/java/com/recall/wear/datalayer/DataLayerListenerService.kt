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
 */
class DataLayerListenerService : WearableListenerService() {

    companion object {
        private const val TAG = "DataLayerListener"
    }

    /**
     * TODO 4: Handle data changes from the phone
     *
     * Called when DataItems are created or modified on a connected device.
     *
     * Steps:
     * 1. Iterate through events using events.forEach { event -> }
     * 2. Check: event.type == DataEvent.TYPE_CHANGED
     * 3. Get path: event.dataItem.uri.path
     * 4. Check: path == "/recall/deck_summary"
     * 5. Extract DataMap: DataMapItem.fromDataItem(event.dataItem).dataMap
     * 6. Read values:
     *    - dataMap.getString("deck_name")
     *    - dataMap.getInt("card_count")
     * 7. Log: Log.d(TAG, "Received deck: $name, cards: $count")
     */
    override fun onDataChanged(events: DataEventBuffer) {
        // TODO: Implement
    }

    /**
     * TODO 5: Handle messages from the phone
     *
     * Called when a message is received from a connected device.
     * Simply log the message path for demonstration.
     *
     * Step: Log.d(TAG, "Message received: ${event.path}")
     */
    override fun onMessageReceived(event: MessageEvent) {
        // TODO: Implement
    }
}
