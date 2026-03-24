package com.recall.app.accessibility

import platform.UIKit.UIAccessibilityPostNotification
import platform.UIKit.UIAccessibilityAnnouncementNotification

/**
 * iOS: Bridge live region announcements to VoiceOver via UIAccessibilityPostNotification.
 *
 * Compose Multiplatform does not map liveRegion semantics to iOS VoiceOver,
 * so we manually post an announcement notification.
 */
actual fun announceForAccessibility(message: String) {
    UIAccessibilityPostNotification(UIAccessibilityAnnouncementNotification, message)
}
