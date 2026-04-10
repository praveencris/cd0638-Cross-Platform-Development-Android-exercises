package com.recall.app.accessibility

/**
 * Posts an accessibility announcement for screen readers.
 *
 * TODO: Implement as an expect function with platform-specific actuals
 * - Android: No-op (liveRegion semantics handle it natively in Compose)
 * - iOS: Bridge to UIAccessibilityPostNotification (Compose Multiplatform
 *   does not yet map liveRegion semantics to VoiceOver)
 *
 * @param message The text to announce to the screen reader
 */
// TODO: Declare expect fun announceForAccessibility(message: String)
