package com.recall.app

import androidx.room.Room
import androidx.room.RoomDatabase
import com.recall.app.data.local.RecallDatabase
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterShortStyle
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

/**
 * Actual implementation of date formatting for iOS
 * Uses NSDateFormatter with locale-aware SHORT style
 * Respects system locale (e.g., MM/DD/YY for US, DD/MM/YY for Europe)
 */
actual fun formatShortDate(epochMillis: Long): String {
    // Convert Unix epoch to NSDate reference date (Jan 1, 2001)
    // NSDate reference is 978307200 seconds after Unix epoch (Jan 1, 1970)
    val timeIntervalSince1970 = epochMillis / 1000.0
    val timeIntervalSinceReferenceDate = timeIntervalSince1970 - 978307200.0
    val date = NSDate(timeIntervalSinceReferenceDate)

    val formatter = NSDateFormatter()
    formatter.dateStyle = NSDateFormatterShortStyle
    formatter.timeStyle = NSDateFormatterNoStyle
    return formatter.stringFromDate(date)
}
