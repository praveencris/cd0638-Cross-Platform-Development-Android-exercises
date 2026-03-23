package com.recall.app.domain

/**
 * A sealed interface representing the outcome of an operation.
 * Used to wrap API responses with type-safe success and error handling.
 */
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "Unknown error"
    ) : Result<Nothing>
}
