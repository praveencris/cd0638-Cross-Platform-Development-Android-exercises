package com.recall.app

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        return "Hello, ${platform.name}! Today is $today."
    }
}
