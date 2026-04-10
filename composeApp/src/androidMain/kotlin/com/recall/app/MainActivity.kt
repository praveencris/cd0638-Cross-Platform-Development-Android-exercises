package com.recall.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

/**
 * Android entry point for the Recall app
 * Simply delegates to the shared Compose UI
 */
class MainActivity : ComponentActivity() {
    // BUG: Can you spot the memory leak in this class? (Hint: static + Context)
    companion object {
        var leak: Context? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Store static reference (causes leak)
        leak = this

        setContent {
            App()
        }
    }
}