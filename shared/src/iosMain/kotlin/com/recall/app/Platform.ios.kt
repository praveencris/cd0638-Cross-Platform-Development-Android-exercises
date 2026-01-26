package com.recall.app

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
