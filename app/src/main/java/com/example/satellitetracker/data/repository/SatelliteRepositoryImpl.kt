package com.example.satellitetracker.data.repository

import android.content.Context
import com.example.satellitetracker.core.result.ApiResult
import com.example.satellitetracker.core.result.Failure
import com.example.satellitetracker.core.result.runCatchingApi
import com.example.satellitetracker.data.dto.PositionsResponseDto
import com.example.satellitetracker.data.dto.SatelliteDetailDto
import com.example.satellitetracker.data.dto.SatelliteDto
import com.example.satellitetracker.data.local.SatelliteDao
import com.example.satellitetracker.data.local.SatelliteDetailEntity
import com.example.satellitetracker.data.local.toSatelliteDetail
import com.example.satellitetracker.di.dispatchers.DispatcherProvider
import com.example.satellitetracker.domain.model.Position
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

    private companion object {
        private const val SATELLITES_FILE = "satellites.json"
        private const val SATELLITE_DETAIL_FILE = "satellite-detail.json"
        private const val POSITIONS_FILE = "positions.json"
    }

    override suspend fun getSatellites(): ApiResult<Failure, List<Satellite>> =
        withContext(dispatcherProvider.io) {
            runCatchingApi {
                val jsonString = context.assets.open(SATELLITES_FILE)
                    .bufferedReader()
                    .use { it.readText() }

                val dataSatellites = json.decodeFromString<List<SatelliteDto>>(jsonString)

                dataSatellites.map { dto ->
                    Satellite(
                        id = dto.id,
                        isActive = dto.active,
                        name = dto.name
                    )
                }
            }
        }

    override suspend fun getSatelliteDetail(id: Int): ApiResult<Failure, SatelliteDetail?> =
        withContext(dispatcherProvider.io) {
            runCatchingApi {
                var detail = satelliteDao.getSatelliteDetail(id)?.toSatelliteDetail()
                if (detail == null) {
                    val jsonString = context.assets.open(SATELLITE_DETAIL_FILE)
                        .bufferedReader()
                        .use { it.readText() }
                    val details: List<SatelliteDetailDto> =
                        json.decodeFromString(jsonString)
                    val found = details.find { it.id == id }
                    detail = found?.let {
                        SatelliteDetail(
                            id = it.id,
                            costPerLaunch = it.costPerLaunch,
                            firstFlight = it.firstFlight,
                            height = it.height,
                            mass = it.mass,
                        )
                    }
                    val ensuredDetail = detail ?: throw NoSuchElementException("Satellite detail not found: " + id)
                    satelliteDao.insertSatelliteDetail(
                        SatelliteDetailEntity(
                            id = ensuredDetail.id,
                            costPerLaunch = ensuredDetail.costPerLaunch,
                            firstFlight = ensuredDetail.firstFlight,
                            height = ensuredDetail.height,
                            mass = ensuredDetail.mass
                        )
                    )
                }
                detail
            }
        }

    override suspend fun getPositions(satelliteId: Int): ApiResult<Failure, PositionList?> =
        withContext(dispatcherProvider.io) {
            runCatchingApi {
                val jsonString = context.assets.open(POSITIONS_FILE)
                    .bufferedReader()
                    .use { it.readText() }
                val response: PositionsResponseDto = json.decodeFromString(jsonString)
                val found = response.list.find { it.id == satelliteId.toString() }
                val positions = found?.let { candidate ->
                    PositionList(
                        id = candidate.id,
                        positions = candidate.positions.map { p ->
                            Position(
                                posX = p.posX,
                                posY = p.posY
                            )
                        }
                    )
                }
                if (positions == null) {
                    throw NoSuchElementException("Positions not found: " + satelliteId)
                }
                positions
            }
        }
}
