package no.uio.ifi.in2000.cellmate.data.repositoryImpl

import no.uio.ifi.in2000.cellmate.data.remote.AreaIdDataSource
import no.uio.ifi.in2000.cellmate.domain.model.hks.priceAreaId
import no.uio.ifi.in2000.cellmate.domain.repository.AreaIdRepository
import javax.inject.Inject

class AreaIdRepositoryImpl @Inject constructor(
    private val dataSource: AreaIdDataSource
): AreaIdRepository {
    override suspend fun getAreaId(postalCode: String):  priceAreaId?{
        return dataSource.fetchPriceAreaId(postalCode)
    }
}