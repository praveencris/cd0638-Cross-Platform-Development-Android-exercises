package com.recall.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform