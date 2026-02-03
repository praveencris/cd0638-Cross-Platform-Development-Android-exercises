package com.recall.wear.tile

import android.content.Intent
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.recall.wear.presentation.MainActivity

/**
 * Tile Service for Quick Access
 * Provides a glanceable surface showing the next deck to study
 *
 * Tiles appear in the Wear OS carousel and allow 5-second interactions
 * without opening the full app.
 *
 * TODO 4: Implement the tile layout
 */
class RecallTileService : TileService() {

    private companion object {
        const val RESOURCES_VERSION = "1"
    }

    /**
     * TODO 4: Implement tile layout
     *
     * This method is called when the system requests the tile content.
     *
     * Implementation requirements:
     * 1. Create mock data (e.g., deckName = "Physics", dueCount = 5)
     * 2. Build a Clickable modifier that launches MainActivity
     * 3. Create a Column layout with two Text elements:
     *    - First text: "$deckName: $dueCount Due" (larger font, white color)
     *    - Second text: "Study Now" (smaller font, blue color, clickable)
     * 4. Wrap the layout in a Timeline and Tile
     * 5. Return the tile using Futures.immediateFuture()
     *
     * ProtoLayout Hints:
     * - Use LayoutElementBuilders.Column.Builder() for vertical layout
     * - Use LayoutElementBuilders.Text.Builder() for text
     * - Use FontStyle.Builder() with .setSize() and .setColor() for styling
     * - Colors use ColorBuilders.argb() with 0xFFRRGGBB format (white = 0xFFFFFFFF)
     * - Sizes use DimensionBuilders.sp() for font size, .dp() for padding
     * - Clickable uses ModifiersBuilders.Clickable with ActionBuilders.LaunchAction
     *
     * Expected behavior:
     * - Shows deck name and due count
     * - Shows "Study Now" button
     * - Tapping "Study Now" launches MainActivity
     */
    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        // TODO 4: Implement tile layout

        // Starter code structure - uncomment and complete:

        // val deckName = "Physics"
        // val dueCount = 5
        //
        // // Create tap action to launch MainActivity
        // val clickable = ModifiersBuilders.Clickable.Builder()
        //     .setOnClick(
        //         ActionBuilders.LaunchAction.Builder()
        //             .setAndroidActivity(
        //                 ActionBuilders.AndroidActivity.Builder()
        //                     .setPackageName(packageName)
        //                     .setClassName(MainActivity::class.java.name)
        //                     .build()
        //             )
        //             .build()
        //     )
        //     .setId("launch_app")
        //     .build()
        //
        // // Build layout with Column and Text elements
        // val layout = LayoutElementBuilders.Column.Builder()
        //     .addContent(
        //         LayoutElementBuilders.Text.Builder()
        //             .setText("$deckName: $dueCount Due")
        //             .setFontStyle(
        //                 LayoutElementBuilders.FontStyle.Builder()
        //                     .setSize(DimensionBuilders.sp(16f))
        //                     .setColor(ColorBuilders.argb(0xFFFFFFFF.toInt()))
        //                     .build()
        //             )
        //             .build()
        //     )
        //     .addContent(
        //         LayoutElementBuilders.Text.Builder()
        //             .setText("Study Now")
        //             .setFontStyle(
        //                 LayoutElementBuilders.FontStyle.Builder()
        //                     .setSize(DimensionBuilders.sp(14f))
        //                     .setColor(ColorBuilders.argb(0xFF64B5F6.toInt()))
        //                     .build()
        //             )
        //             .setModifiers(
        //                 ModifiersBuilders.Modifiers.Builder()
        //                     .setClickable(clickable)
        //                     .setPadding(
        //                         ModifiersBuilders.Padding.Builder()
        //                             .setTop(DimensionBuilders.dp(8f))
        //                             .build()
        //                     )
        //                     .build()
        //             )
        //             .build()
        //     )
        //     .setModifiers(
        //         ModifiersBuilders.Modifiers.Builder()
        //             .setPadding(
        //                 ModifiersBuilders.Padding.Builder()
        //                     .setAll(DimensionBuilders.dp(16f))
        //                     .build()
        //             )
        //             .build()
        //     )
        //     .build()
        //
        // // Wrap in timeline and tile
        // val tile = TileBuilders.Tile.Builder()
        //     .setResourcesVersion(RESOURCES_VERSION)
        //     .setTileTimeline(
        //         TimelineBuilders.Timeline.Builder()
        //             .addTimelineEntry(
        //                 TimelineBuilders.TimelineEntry.Builder()
        //                     .setLayout(
        //                         LayoutElementBuilders.Layout.Builder()
        //                             .setRoot(layout)
        //                             .build()
        //                     )
        //                     .build()
        //             )
        //             .build()
        //     )
        //     .build()
        //
        // return Futures.immediateFuture(tile)

        // For now, return an empty tile until students implement
        return Futures.immediateFuture(
            TileBuilders.Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTileTimeline(
                    TimelineBuilders.Timeline.Builder()
                        .addTimelineEntry(
                            TimelineBuilders.TimelineEntry.Builder()
                                .setLayout(
                                    LayoutElementBuilders.Layout.Builder()
                                        .setRoot(
                                            LayoutElementBuilders.Text.Builder()
                                                .setText("TODO: Implement tile layout")
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build()
        )
    }

    /**
     * Called when the system requests tile resources
     * Returns an empty resource bundle (we're not using custom images)
     *
     * This is already implemented for you - no changes needed
     */
    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> {
        return Futures.immediateFuture(
            ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .build()
        )
    }
}
