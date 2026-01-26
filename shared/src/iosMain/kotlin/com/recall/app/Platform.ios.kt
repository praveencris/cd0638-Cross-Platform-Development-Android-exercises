package com.recall.app

import platform.Foundation.NSUUID
import platform.UIKit.UIDevice

/**
 * iOS platform implementation
 */
class IOSPlatform : Platform {
    override val name: String = "iOS ${UIDevice.currentDevice.systemVersion}"
}

/**
 * Actual implementation of getPlatform() for iOS
 */
actual fun getPlatform(): Platform = IOSPlatform()

/**
 * Actual implementation of UUID generation for iOS
 * Uses Foundation's NSUUID for native UUID generation
 */
actual fun generateUUID(): String = NSUUID().UUIDString
