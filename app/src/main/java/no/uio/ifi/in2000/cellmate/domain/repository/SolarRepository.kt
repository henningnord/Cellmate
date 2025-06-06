package no.uio.ifi.in2000.cellmate.domain.repository

import no.uio.ifi.in2000.cellmate.domain.model.solar.SolarResponse

interface SolarRepository {
    suspend fun getSolarData(lat: Double, lon: Double): SolarResponse?
}