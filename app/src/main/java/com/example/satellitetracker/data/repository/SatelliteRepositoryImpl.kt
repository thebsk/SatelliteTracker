package com.example.satellitetracker.data.repository

import com.example.satellitetracker.core.result.ApiResult
import com.example.satellitetracker.core.result.Failure
import com.example.satellitetracker.core.result.runCatchingApi
import com.example.satellitetracker.core.util.AssetFileReader
import com.example.satellitetracker.data.dto.PositionsResponseDto
import com.example.satellitetracker.data.dto.SatelliteDetailDto
import com.example.satellitetracker.data.dto.SatelliteDto
import com.example.satellitetracker.data.mapper.DtoMapper
import com.example.satellitetracker.data.local.SatelliteDao
import com.example.satellitetracker.data.local.SatelliteDetailEntity
import com.example.satellitetracker.data.local.toSatelliteDetail
import com.example.satellitetracker.di.dispatchers.DispatcherProvider
import com.example.satellitetracker.domain.model.Position
import com.example.satellitetracker.domain.model.PositionList
import com.example.satellitetracker.domain.model.Satellite
import com.example.satellitetracker.domain.model.SatelliteDetail
import com.example.satellitetracker.domain.repository.SatelliteRepository
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SatelliteRepositoryImpl @Inject constructor(
    private val assetFileReader: AssetFileReader,
    private val satelliteDao: SatelliteDao,
    private val json: Json,
    private val dispatcherProvider: DispatcherProvider,
    private val dtoMapper: DtoMapper
) : SatelliteRepository {

    private companion object {
        private const val SATELLITES_FILE = "satellites.json"
        private const val SATELLITE_DETAIL_FILE = "satellite-detail.json"
        private const val POSITIONS_FILE = "positions.json"
    }

    override suspend fun getSatellites(): ApiResult<Failure, List<Satellite>> =
        withContext(dispatcherProvider.io) {
            runCatchingApi {
                val jsonString = assetFileReader.readAssetFile(SATELLITES_FILE)
                val dataSatellites = json.decodeFromString<List<SatelliteDto>>(jsonString)
                dataSatellites.map { dtoMapper.toDomain(it) }
            }
        }

    override suspend fun getSatelliteDetail(id: Int): ApiResult<Failure, SatelliteDetail?> =
        withContext(dispatcherProvider.io) {
            runCatchingApi {
                var detail = satelliteDao.getSatelliteDetail(id)?.toSatelliteDetail()
                if (detail == null) {
                    val jsonString = assetFileReader.readAssetFile(SATELLITE_DETAIL_FILE)
                    val details: List<SatelliteDetailDto> =
                        json.decodeFromString(jsonString)
                    val found = details.find { it.id == id }
                    detail = found?.let { dtoMapper.toDomain(it) }
                    detail?.let { ensuredDetail ->
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
                }
                detail
            }
        }

    override suspend fun getPositions(satelliteId: Int): ApiResult<Failure, PositionList?> =
        withContext(dispatcherProvider.io) {
            runCatchingApi {
                val jsonString = assetFileReader.readAssetFile(POSITIONS_FILE)
                val response: PositionsResponseDto = json.decodeFromString(jsonString)
                val found = response.list.find { it.id == satelliteId.toString() }
                val positions = found?.let { dtoMapper.toDomain(it) }
                positions
            }
        }
}
