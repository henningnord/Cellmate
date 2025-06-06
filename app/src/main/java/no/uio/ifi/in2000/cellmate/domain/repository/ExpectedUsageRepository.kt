package no.uio.ifi.in2000.cellmate.domain.repository

import no.uio.ifi.in2000.cellmate.data.local.database.entity.ExpectedUsageEntity

interface ExpectedUsageRepository {
    suspend fun insert(entity: ExpectedUsageEntity)
    suspend fun get(lat: Double, lon: Double, minTimestamp: Long): ExpectedUsageEntity?
}