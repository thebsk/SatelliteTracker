package com.example.satellitetracker.core.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed class ApiResult<out E, out S> {
    /** Represents the left side of [ApiResult] class which by convention is a "Failure". */
    data class Error<out E>(val error: E) : ApiResult<E, Nothing>()

    /** Represents the right side of [ApiResult] class which by convention is a "Success". */
    data class Success<out R>(val data: R) : ApiResult<Nothing, R>()

    val success: S get() = (this as Success<S>).data

    fun <R> success(b: R) = Success(b)
}

// Credits to Alex Hart -> https://proandroiddev.com/kotlins-nothing-type-946de7d464fb
// Composes 2 functions
fun <A, B, C> (suspend (A) -> B).c(f: (B) -> C): suspend (A) -> C = {
    f(this(it))
}

suspend fun <T, L, R> ApiResult<L, R>.flatMap(data: suspend (R) -> ApiResult<L, T>): ApiResult<L, T> =
    when (this) {
        is ApiResult.Error -> ApiResult.Error(error)
        is ApiResult.Success -> data(this.data)
    }

suspend fun <T, L, R> ApiResult<L, R>.map(data: suspend (R) -> (T)): ApiResult<L, T> =
    this.flatMap(data.c(::success))

fun <T, L, R> Flow<ApiResult<L, R>>.mapSuccessData(mapper: suspend (R) -> (T)): Flow<ApiResult<L, T>> {
    return map { result -> result.map(mapper) }
}
