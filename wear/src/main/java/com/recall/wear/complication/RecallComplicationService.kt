package com.recall.wear.complication

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import com.recall.wear.presentation.MainActivity

/**
 * Complication Service for Watch Face Integration
 * Provides a SHORT_TEXT complication showing the number of due cards
 *
 * This allows users to see their due card count directly on their watch face
 * and tap to open the full app.
 */
class RecallComplicationService : ComplicationDataSourceService() {

    /**
     * Called when the watch face requests complication data
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

        /**
         * TODO 2: Replace mock due count with real API data
         *
         * Current code uses: val dueCount = 12
         *
         * Your task:
         * 1. Get RecallApiService from Koin using KoinPlatform.getKoin().get<RecallApiService>()
         * 2. Fetch all decks using runBlocking { apiService.fetchAllDecks() }
         * 3. Calculate total cards: decks.sumOf { it.cards.size }
         * 4. Wrap in try/catch — return 0 if the API call fails
         * 5. Assign result to dueCount variable
         *
         * Required imports (add at top of file):
         *   import kotlinx.coroutines.runBlocking
         *   import org.koin.mp.KoinPlatform
         *   import com.recall.app.network.RecallApiService
         */
        val dueCount = 12  // ← Replace with real data

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
     */
    override fun getPreviewData(type: ComplicationType): ComplicationData {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder("12").build(),
            contentDescription = PlainComplicationText.Builder("12 cards due").build()
        ).build()
    }
}
