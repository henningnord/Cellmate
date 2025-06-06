package no.uio.ifi.in2000.cellmate.domain.usecase

import javax.inject.Inject
import no.uio.ifi.in2000.cellmate.domain.repository.AreaIdRepository
import no.uio.ifi.in2000.cellmate.domain.model.hks.priceAreaId

class AreaIdUseCase @Inject constructor(
    private val areaIdRepository : AreaIdRepository
) {
    suspend fun fetchPriceAreaId(postalCode: String): priceAreaId?{
        return areaIdRepository.getAreaId(postalCode)
    }
}