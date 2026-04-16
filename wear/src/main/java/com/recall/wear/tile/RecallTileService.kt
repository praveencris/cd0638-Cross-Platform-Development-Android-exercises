package com.recall.wear.tile

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
import com.recall.wear.data.DeckSnapshotStore
import com.recall.wear.presentation.MainActivity
import org.koin.mp.KoinPlatform

/**
 * Tile Service for Quick Access
 * Provides a glanceable surface showing the next deck to study
 *
 * Tiles appear in the Wear OS carousel and allow 5-second interactions
 * without opening the full app.
 *
 * This implementation reads the last local deck snapshot so tile callbacks
 * stay responsive without blocking on network work.
 */
class RecallTileService : TileService() {

    private companion object {
        const val RESOURCES_VERSION = "1"
    }

    /**
     * Called when the system requests the tile layout
     * Returns a timeline with the tile content
     *
     * Reads the last deck snapshot from local storage.
     */
    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        val snapshotStore = KoinPlatform.getKoin().get<DeckSnapshotStore>()
        val snapshot = snapshotStore.readSnapshot()
        val deckName = snapshot.featuredDeckName
        val dueCount = snapshot.dueCount

        // Create the tap action to launch MainActivity
        val clickable = ModifiersBuilders.Clickable.Builder()
            .setOnClick(
                ActionBuilders.LaunchAction.Builder()
                    .setAndroidActivity(
                        ActionBuilders.AndroidActivity.Builder()
                            .setPackageName(packageName)
                            .setClassName(MainActivity::class.java.name)
                            .build()
                    )
                    .build()
            )
            .setId("launch_app")
            .build()

        // Build a simple column layout with text
        val layout = LayoutElementBuilders.Column.Builder()
            .addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText("$deckName: $dueCount Due")
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .setSize(DimensionBuilders.sp(16f))
                            .setColor(ColorBuilders.argb(0xFFFFFFFF.toInt()))
                            .build()
                    )
                    .build()
            )
            .addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText("Study Now")
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .setSize(DimensionBuilders.sp(14f))
                            .setColor(ColorBuilders.argb(0xFF64B5F6.toInt()))
                            .build()
                    )
                    .setModifiers(
                        ModifiersBuilders.Modifiers.Builder()
                            .setClickable(clickable)
                            .setPadding(
                                ModifiersBuilders.Padding.Builder()
                                    .setTop(DimensionBuilders.dp(8f))
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setPadding(
                        ModifiersBuilders.Padding.Builder()
                            .setAll(DimensionBuilders.dp(16f))
                            .build()
                    )
                    .build()
            )
            .build()

        // Wrap in timeline and tile
        val tile = TileBuilders.Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION)
            .setTileTimeline(
                TimelineBuilders.Timeline.Builder()
                    .addTimelineEntry(
                        TimelineBuilders.TimelineEntry.Builder()
                            .setLayout(
                                LayoutElementBuilders.Layout.Builder()
                                    .setRoot(layout)
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build()

        return Futures.immediateFuture(tile)
    }

    /**
     * Called when the system requests tile resources
     * Returns an empty resource bundle (we're not using custom images)
     */
    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> {
        return Futures.immediateFuture(
            ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .build()
        )
    }
}
