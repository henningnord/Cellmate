package no.uio.ifi.in2000.cellmate.domain.repository

import no.uio.ifi.in2000.cellmate.domain.model.hks.priceAreaId

interface AreaIdRepository {
   suspend fun getAreaId(postalCode : String): priceAreaId?
}