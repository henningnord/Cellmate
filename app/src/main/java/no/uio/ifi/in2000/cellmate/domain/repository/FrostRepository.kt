package no.uio.ifi.in2000.cellmate.domain.repository

import no.uio.ifi.in2000.cellmate.domain.model.frost.ObservationResponse

interface FrostRepository {
    suspend fun getFrostData(elements: List<String>, lat: Double, lon: Double): ObservationResponse?
}
