package com.recall.wear.complication

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import com.recall.wear.data.DeckSnapshotStore
import com.recall.wear.presentation.MainActivity
import org.koin.mp.KoinPlatform

/**
 * Complication Service for Watch Face Integration
 * Provides a SHORT_TEXT complication showing the number of due cards
 *
 * This allows users to see their due card count directly on their watch face
 * and tap to open the full app.
 *
 * This implementation reads the last on-device deck snapshot so complication
 * callbacks do not block on network work.
 */
class RecallComplicationService : ComplicationDataSourceService() {

    /**
     * Called when the watch face requests complication data
     * Reads the last saved deck snapshot from local storage.
     */
    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener
    ) {
        // Return null if the complication type isn't supported
        if (request.complicationType != ComplicationType.SHORT_TEXT) {
            listener.onComplicationData(null)
            return
        }

        val snapshotStore = KoinPlatform.getKoin().get<DeckSnapshotStore>()
        val dueCount = snapshotStore.readSnapshot().dueCount

        // Create a PendingIntent to launch MainActivity when the complication is tapped
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build and return the complication data
        val complicationData = ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(dueCount.toString()).build(),
            contentDescription = PlainComplicationText.Builder("$dueCount cards due").build()
        )
            .setTapAction(pendingIntent)
            .build()

        listener.onComplicationData(complicationData)
    }

    /**
     * Preview data for the complication editor
     * Shown when users are customizing their watch face
     * Uses static values since preview is shown before any data is loaded
     */
    override fun getPreviewData(type: ComplicationType): ComplicationData {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder("12").build(),
            contentDescription = PlainComplicationText.Builder("12 cards due").build()
        ).build()
    }
}
