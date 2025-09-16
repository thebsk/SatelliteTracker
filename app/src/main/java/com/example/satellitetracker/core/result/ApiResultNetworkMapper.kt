package com.example.satellitetracker.core.result

import com.example.satellitetracker.data.mapper.toFailure

inline fun <R> runCatchingApi(
    block: () -> R,
): ApiResult<Failure, R> = try {
    ApiResult.Success(block())
} catch (t: Throwable) {
    ApiResult.Error(t.toFailure())
}
