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
 */
class WearDataLayerService(context: Context) {

    private val dataClient = Wearable.getDataClient(context)
    private val messageClient = Wearable.getMessageClient(context)
    private val capabilityClient = Wearable.getCapabilityClient(context)

    /**
     * TODO 1: Find the connected phone node
     *
     * Use CapabilityClient to discover devices with the "recall_phone_app" capability.
     *
     * Steps:
     * 1. Call capabilityClient.getCapability("recall_phone_app", CapabilityClient.FILTER_REACHABLE)
     * 2. Call .await() on the result (this is a suspend function)
     * 3. Return capabilityInfo.nodes.firstOrNull()
     *
     * @return The first reachable phone Node, or null if no phone is connected
     */
    suspend fun findPhoneNode(): Node? {
        // TODO: Implement
        return null
    }

    /**
     * TODO 2: Send a refresh request to the phone
     *
     * Use MessageClient to send a "/recall/refresh" message to the phone.
     * Messages are fire-and-forget — delivered once, not persisted.
     *
     * Steps:
     * 1. Call findPhoneNode() to get the connected phone
     * 2. If node is null, return early (no phone connected)
     * 3. Call messageClient.sendMessage(node.id, "/recall/refresh", byteArrayOf())
     * 4. Call .await() on the result
     */
    suspend fun sendRefreshRequest() {
        // TODO: Implement
    }

    /**
     * TODO 3: Sync deck summary data via DataClient
     *
     * Use DataClient to write a DataItem at path "/recall/deck_summary".
     * DataItems are automatically synced to all connected devices and persisted.
     *
     * Steps:
     * 1. Create: PutDataMapRequest.create("/recall/deck_summary")
     * 2. In the apply { } block, put into dataMap:
     *    - "deck_name" (String) using dataMap.putString()
     *    - "card_count" (Int) using dataMap.putInt()
     *    - "timestamp" (Long, System.currentTimeMillis()) using dataMap.putLong()
     * 3. Convert: .asPutDataRequest().setUrgent()
     * 4. Call: dataClient.putDataItem(request).await()
     *
     * The timestamp ensures each write is treated as new data.
     *
     * @param deckName The name of the deck to sync
     * @param cardCount The number of cards in the deck
     */
    suspend fun syncDeckSummary(deckName: String, cardCount: Int) {
        // TODO: Implement
    }
}
