package com.example.satellitetracker.data.mapper

import com.example.satellitetracker.core.result.Failure
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.sql.SQLException
import java.util.NoSuchElementException

fun Throwable.toFailure(): Failure = when (this) {
    is UnknownHostException -> Failure.NetworkUnavailable
    is SocketTimeoutException -> Failure.Timeout
    is HttpException -> {
        when (code()) {
            401 -> Failure.Unauthorized
            403 -> Failure.Forbidden
            404 -> Failure.NotFound
            in 400..499 -> Failure.ClientError(code())
            in 500..599 -> Failure.ServerError(code())
            else -> Failure.Unexpected(this)
        }
    }

    is SQLException -> Failure.DatabaseError
    is NoSuchElementException -> Failure.NotFound
    else -> Failure.Unexpected(this)
}