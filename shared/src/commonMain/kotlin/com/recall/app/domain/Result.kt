package com.recall.app.domain

/**
 * Type-safe wrapper for operations that can fail.
 */
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "Unknown error"
    ) : Result<Nothing>
}
