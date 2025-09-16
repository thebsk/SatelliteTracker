package com.example.satellitetracker.core.result

sealed interface Failure {
    // Common
    data class Unexpected(val cause: Throwable? = null) : Failure

    // Network
    data object NetworkUnavailable : Failure
    data object Timeout : Failure

    // API
    data object Unauthorized : Failure      // 401
    data object Forbidden : Failure         // 403
    data object NotFound : Failure          // 404
    data class ServerError(val code: Int) : Failure // 5xx
    data class ClientError(val code: Int) : Failure // 4xx generic

    // DB
    data object DatabaseError : Failure
}
