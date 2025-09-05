package com.example.satellitetracker.core.domain.repository

import android.content.Context
import com.example.satellitetracker.data.model.Satellite
import com.example.satellitetracker.data.model.SatelliteDetail
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SatelliteRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) : SatelliteRepository {

    override suspend fun getSatellites(): List<Satellite> {
        return try {
            val jsonString = context.assets.open("satellites.json").bufferedReader().use { it.readText() }
            json.decodeFromString<List<Satellite>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getSatelliteDetail(id: Int): SatelliteDetail? {
        return try {
            val jsonString = context.assets.open("satellite-detail.json").bufferedReader().use { it.readText() }
            val details = json.decodeFromString<List<SatelliteDetail>>(jsonString)
            details.find { it.id == id }
        } catch (e: Exception) {
            null
        }
    }
}
