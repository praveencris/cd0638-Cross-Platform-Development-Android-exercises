package com.recall.app.accessibility

/**
 * Posts an accessibility announcement for screen readers.
 *
 * On Android, this is a no-op because liveRegion semantics handle announcements natively.
 * On iOS, this bridges to UIAccessibilityPostNotification since Compose Multiplatform
 * does not yet map liveRegion semantics to VoiceOver announcements.
 *
 * @param message The text to announce to the screen reader
 */
expect fun announceForAccessibility(message: String)
