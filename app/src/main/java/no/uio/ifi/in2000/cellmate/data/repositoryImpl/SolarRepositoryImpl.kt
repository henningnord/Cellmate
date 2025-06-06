package no.uio.ifi.in2000.cellmate.data.repositoryImpl

import android.util.Log
import no.uio.ifi.in2000.cellmate.data.local.database.datasource.LocalSolarDataSource
import no.uio.ifi.in2000.cellmate.data.remote.SolarDataSource
import no.uio.ifi.in2000.cellmate.domain.model.solar.SolarResponse
import no.uio.ifi.in2000.cellmate.domain.repository.SolarRepository
import javax.inject.Inject

class SolarRepositoryImpl @Inject constructor(
    private val dataSource: SolarDataSource,
    private val localDataSource: LocalSolarDataSource
) : SolarRepository {
    override suspend fun getSolarData(lat: Double, lon: Double): SolarResponse? {
        val cached = localDataSource.getCachedRoofSize(lat, lon)
        if (cached != null) {
            Log.d("SolarRepositoryImpl", "Using cached roof size: $cached")
            return dataSource.fetchSolarInfo(lat, lon)
        }
        // If not in cache, fetch from API and cache
        val result = dataSource.fetchSolarInfo(lat, lon)
        if (result != null) {
            Log.d("SolarRepositoryImpl", "Fetched new roof size: ${result.solarPotential?.roofSegmentStats?.size}")
            val roofSize = result.solarPotential?.roofSegmentStats?.size?.toDouble() ?: 0.0
            localDataSource.saveRoofSize(lat, lon, roofSize)
        }
        return result
    }

}