package com.example.satellitetracker.data.repository

import android.content.Context
import com.example.satellitetracker.data.local.SatelliteDao
import com.example.satellitetracker.data.local.toSatelliteDetail
import com.example.satellitetracker.data.model.PositionsResponse
import com.example.satellitetracker.di.dispatchers.DispatcherProvider
import com.example.satellitetracker.domain.model.PositionList
import com.example.satellitetracker.domain.model.Satellite
import com.example.satellitetracker.domain.model.SatelliteDetail
import com.example.satellitetracker.domain.repository.SatelliteRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SatelliteRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val satelliteDao: SatelliteDao,
    private val json: Json,
    private val dispatcherProvider: DispatcherProvider
) : SatelliteRepository {

    override suspend fun getSatellites(): List<Satellite> = withContext(dispatcherProvider.io) {
        try {
            val jsonString =
                context.assets.open("satellites.json").bufferedReader().use { it.readText() }
            val dataSatellites =
                json.decodeFromString<List<com.example.satellitetracker.data.model.Satellite>>(
                    jsonString
                )
            dataSatellites.map { Satellite(id = it.id, isActive = it.active, name = it.name) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getSatelliteDetail(id: Int): SatelliteDetail? =
        withContext(dispatcherProvider.io) {
            var detail = satelliteDao.getSatelliteDetail(id)?.toSatelliteDetail()
            if (detail == null) {
                val jsonString = context.assets.open("satellite-detail.json").bufferedReader()
                    .use { it.readText() }
                val details: List<com.example.satellitetracker.data.model.SatelliteDetail> =
                    json.decodeFromString(jsonString)
                val found = details.find { it.id == id }
                detail = found?.let {
                    SatelliteDetail(
                        id = it.id,
                        costPerLaunch = it.costPerLaunch,
                        firstFlight = it.firstFlight,
                        height = it.height,
                        mass = it.mass
                    )
                }
                detail?.let {
                    satelliteDao.insertSatelliteDetail(
                        com.example.satellitetracker.data.local.SatelliteDetailEntity(
                            id = it.id,
                            costPerLaunch = it.costPerLaunch,
                            firstFlight = it.firstFlight,
                            height = it.height,
                            mass = it.mass
                        )
                    )
                }
            }
            detail
        }

    override suspend fun getPositions(satelliteId: Int): PositionList? =
        withContext(dispatcherProvider.io) {
            val jsonString =
                context.assets.open("positions.json").bufferedReader().use { it.readText() }
            val response: PositionsResponse = json.decodeFromString(jsonString)
            val found = response.list.find { it.id == satelliteId.toString() }
            found?.let {
                PositionList(
                    id = it.id,
                    positions = it.positions.map { p ->
                        com.example.satellitetracker.domain.model.Position(
                            posX = p.posX,
                            posY = p.posY
                        )
                    }
                )
            }
        }
}
