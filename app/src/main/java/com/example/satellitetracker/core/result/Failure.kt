package com.example.satellitetracker.core.result

sealed interface Failure {
    // Common
    data class Unexpected(val cause: Throwable? = null) : Failure

    // Network
    data object NetworkUnavailable : Failure
    data object Timeout : Failure

    // HTTP / API
    data object Unauthorized : Failure      // 401
    data object Forbidden : Failure         // 403
    data object NotFound : Failure          // 404
    data class ServerError(val code: Int) : Failure // 5xx
    data class ClientError(val code: Int) : Failure // 4xx generic

    // DB / Cache
    data object DatabaseError : Failure
}

fun Failure.toUserMessage(): String = when (this) {
    is Failure.NetworkUnavailable -> "No internet connection."
    is Failure.Timeout -> "Request timed out. Please try again."
    is Failure.Unauthorized -> "You are not authorized."
    is Failure.Forbidden -> "Access denied."
    is Failure.NotFound -> "Not found."
    is Failure.ServerError -> "Server error. Please try later."
    is Failure.ClientError -> "Something went wrong."
    is Failure.DatabaseError -> "Local database error."
    is Failure.Unexpected -> this.cause?.message ?: "Unexpected error occurred."
}
