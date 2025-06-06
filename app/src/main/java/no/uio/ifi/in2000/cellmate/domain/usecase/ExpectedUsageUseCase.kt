package no.uio.ifi.in2000.cellmate.domain.usecase

import android.util.Log
import no.uio.ifi.in2000.cellmate.data.local.database.entity.ExpectedUsageEntity
import no.uio.ifi.in2000.cellmate.domain.repository.ExpectedUsageRepository
import javax.inject.Inject

class ExpectedUsageUseCase @Inject constructor(
    private val repository: ExpectedUsageRepository
) {
    companion object {
        private const val CACHE_EXPIRATION = 7 * 24 * 60 * 60 * 1000L
    }
    suspend fun cacheUsage(
        lat: Double,
        lon: Double,
        expectedUsage: Int?,
        roofSize: Int?
    ) {
        Log.i("ExpectedUsageUseCase", "cacheUsage: lat: $lat, lon: $lon, expectedUsage: $expectedUsage, roofSize: $roofSize")
        val size = roofSize ?: return
        val entity = ExpectedUsageEntity(
            latitude = lat,
            longitude = lon,
            expectedEnergyUsageYearly = expectedUsage,
            roofSize = size,
            timestamp = System.currentTimeMillis()
        )
        repository.insert(entity)
    }

    suspend fun getCachedUsage(lat: Double, lon: Double): ExpectedUsageEntity? {
        Log.i("ExpectedUsageUseCase", "getCachedUsage: lat: $lat, lon: $lon")
        val timestamp = System.currentTimeMillis() - CACHE_EXPIRATION
        return repository.get(lat, lon, timestamp)
    }

}