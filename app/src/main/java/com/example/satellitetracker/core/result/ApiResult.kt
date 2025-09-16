package com.example.satellitetracker.core.result

sealed class ApiResult<out E, out S> {
    /** Represents the left side of [ApiResult] class which by convention is a "Failure". */
    data class Error<out E>(val error: E) : ApiResult<E, Nothing>()

    /** Represents the right side of [ApiResult] class which by convention is a "Success". */
    data class Success<out R>(val data: R) : ApiResult<Nothing, R>()

    val success: S get() = (this as Success<S>).data

    fun <R> success(b: R) = Success(b)
}
