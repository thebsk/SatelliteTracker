package com.example.satellitetracker.presentation

import android.content.Context
import com.example.satellitetracker.R
import com.example.satellitetracker.core.result.Failure
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface ErrorMessageProvider {
    fun fromFailure(failure: Failure): String
}

class ErrorMessageProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ErrorMessageProvider {
    override fun fromFailure(failure: Failure): String = when (failure) {
        is Failure.NetworkUnavailable -> context.getString(R.string.error_network_unavailable)
        is Failure.Timeout -> context.getString(R.string.error_timeout)
        is Failure.Unauthorized -> context.getString(R.string.error_unauthorized)
        is Failure.Forbidden -> context.getString(R.string.error_forbidden)
        is Failure.NotFound -> context.getString(R.string.error_not_found)
        is Failure.ServerError -> context.getString(R.string.error_server)
        is Failure.ClientError -> context.getString(R.string.error_client)
        is Failure.DatabaseError -> context.getString(R.string.error_database)
        is Failure.Unexpected -> failure.cause?.message ?: context.getString(R.string.error_unexpected)
    }
}


