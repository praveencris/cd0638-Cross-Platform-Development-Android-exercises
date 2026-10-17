package com.recall.app.network

 //DONE: Uncomment after adding Ktor Darwin engine dependency
 import io.ktor.client.engine.*
 import io.ktor.client.engine.darwin.*

 //DONE: Uncomment after adding Ktor dependencies
 actual fun createHttpEngine(): HttpClientEngine = Darwin.create()
