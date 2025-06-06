package no.uio.ifi.in2000.cellmate.data.repositoryImpl

import no.uio.ifi.in2000.cellmate.data.local.database.dao.ExpectedUsageDao
import no.uio.ifi.in2000.cellmate.data.local.database.entity.ExpectedUsageEntity
import no.uio.ifi.in2000.cellmate.domain.repository.ExpectedUsageRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpectedUsageRepositoryImpl @Inject constructor(
    private val dao: ExpectedUsageDao
) : ExpectedUsageRepository {

    override suspend fun insert(entity: ExpectedUsageEntity) {
        dao.insertExpectedUsage(entity)
    }

    override suspend fun get(lat: Double, lon: Double, minTimestamp: Long): ExpectedUsageEntity? {
        return dao.getExpectedUsage(lat, lon, minTimestamp)
    }
}