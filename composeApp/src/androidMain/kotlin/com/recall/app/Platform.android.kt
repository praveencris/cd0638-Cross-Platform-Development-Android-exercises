package com.recall.app

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Droid ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()