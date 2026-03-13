package com.recall.wear.datalayer

import android.content.Context
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

/**
 * Wearable Data Layer Service
 *
 * Wraps the three core Data Layer APIs for inter-device communication:
 * - DataClient: Sync structured data between phone and watch
 * - MessageClient: Send one-off commands between devices
 * - CapabilityClient: Discover which connected devices support specific features
 *
 * Note: In the real Recall architecture, the watch app accesses shared KMP code
 * directly via Koin, making the Data Layer redundant for data access. This class
 * exists as a teaching demonstration of the Wearable Data Layer API pattern.
 */
class WearDataLayerService(context: Context) {

    private val dataClient = Wearable.getDataClient(context)
    private val messageClient = Wearable.getMessageClient(context)
    private val capabilityClient = Wearable.getCapabilityClient(context)

    /**
     * Find a connected phone node that has the Recall phone app installed.
     *
     * Uses CapabilityClient to discover devices advertising the "recall_phone_app"
     * capability. Only reachable (currently connected) nodes are returned.
     *
     * @return The first reachable phone Node, or null if no phone is connected
     */
    suspend fun findPhoneNode(): Node? {
        val capabilityInfo = capabilityClient
            .getCapability("recall_phone_app", CapabilityClient.FILTER_REACHABLE)
            .await()
        return capabilityInfo.nodes.firstOrNull()
    }

    /**
     * Send a refresh request to the connected phone app.
     *
     * Uses MessageClient to send a one-off "/recall/refresh" message.
     * Messages are fire-and-forget — they are delivered once and not persisted.
     * This is ideal for triggering actions (like "please refresh your data").
     *
     * If no phone is connected, this method returns silently.
     */
    suspend fun sendRefreshRequest() {
        val node = findPhoneNode() ?: return
        messageClient.sendMessage(
            node.id,
            "/recall/refresh",
            byteArrayOf()
        ).await()
    }

    /**
     * Sync a deck summary to the Data Layer.
     *
     * Uses DataClient to write a DataItem at path "/recall/deck_summary".
     * DataItems are automatically synced to all connected devices and persisted
     * by Google Play Services until overwritten or deleted. This is ideal for
     * sharing state (like "which deck has the most due cards").
     *
     * The timestamp field ensures each write is treated as a new value,
     * triggering onDataChanged() on the receiving device.
     *
     * @param deckName The name of the deck to sync
     * @param cardCount The number of cards in the deck
     */
    suspend fun syncDeckSummary(deckName: String, cardCount: Int) {
        val request = PutDataMapRequest.create("/recall/deck_summary").apply {
            dataMap.putString("deck_name", deckName)
            dataMap.putInt("card_count", cardCount)
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }.asPutDataRequest().setUrgent()

        dataClient.putDataItem(request).await()
    }
}
