package com.recall.app.accessibility

/**
 * Android: No-op. Live region announcements are handled natively
 * by the liveRegion semantics property in Compose.
 */
actual fun announceForAccessibility(message: String) {
    // Android uses LiveRegionMode.Polite via Compose semantics
    // No additional bridge needed
}
