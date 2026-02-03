package com.recall.app

import androidx.room.Room
import androidx.room.RoomDatabase
import com.recall.app.data.local.RecallDatabase
import platform.Foundation.NSHomeDirectory
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

/**
 * Actual implementation of database builder for iOS
 * Creates a Room database in the app's home directory
 */
actual fun getDatabaseBuilder(): RoomDatabase.Builder<RecallDatabase> {
    val dbFile = NSHomeDirectory() + "/recall.db"
    return Room.databaseBuilder<RecallDatabase>(name = dbFile)
}
