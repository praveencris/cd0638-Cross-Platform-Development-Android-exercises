package com.recall.wear.complication

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationText
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
 *
 * TODO 3: Implement the complication data
 */
class RecallComplicationService : ComplicationDataSourceService() {

    /**
     * TODO 3: Implement complication request handler
     *
     * This method is called when the watch face requests complication data.
     *
     * Implementation requirements:
     * 1. Check if request.complicationType == ComplicationType.SHORT_TEXT
     *    - If not, call listener.onComplicationData(null) and return
     * 2. Create a mock due count (e.g., 12 cards)
     * 3. Create a PendingIntent to launch MainActivity when tapped
     * 4. Build a ShortTextComplicationData with:
     *    - text: The due count as a string (e.g., "12")
     *    - contentDescription: Description for accessibility (e.g., "12 cards due")
     *    - tapAction: The PendingIntent you created
     * 5. Call listener.onComplicationData(complicationData)
     *
     * Hints:
     * - Use PlainComplicationText.Builder(text).build() to create text
     * - Use ShortTextComplicationData.Builder() to create the complication
     * - PendingIntent flags: PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
     *
     * Expected behavior:
     * - Returns null for unsupported complication types
     * - Returns SHORT_TEXT with due count for supported types
     * - Tapping the complication launches the app
     */
    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener
    ) {
        // TODO 3: Implement complication data
        // For now, return null until students implement
        listener.onComplicationData(null)

        // Starter code structure:
        // if (request.complicationType != ComplicationType.SHORT_TEXT) {
        //     listener.onComplicationData(null)
        //     return
        // }
        //
        // val dueCount = 12 // Mock value
        //
        // val intent = Intent(this, MainActivity::class.java)
        // val pendingIntent = PendingIntent.getActivity(
        //     this,
        //     0,
        //     intent,
        //     PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        // )
        //
        // val complicationData = ShortTextComplicationData.Builder(
        //     text = PlainComplicationText.Builder(dueCount.toString()).build(),
        //     contentDescription = PlainComplicationText.Builder("$dueCount cards due").build()
        // )
        //     .setTapAction(pendingIntent)
        //     .build()
        //
        // listener.onComplicationData(complicationData)
    }

    /**
     * Preview data for the complication editor
     * Shown when users are customizing their watch face
     *
     * This is already implemented for you - it shows "12" as preview text
     */
    override fun getPreviewData(type: ComplicationType): ComplicationData {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder("12").build(),
            contentDescription = PlainComplicationText.Builder("12 cards due").build()
        ).build()
    }
}
