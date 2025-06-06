package no.uio.ifi.in2000.cellmate.domain.usecase

import android.util.Log
import no.uio.ifi.in2000.cellmate.domain.repository.SolarRepository
import javax.inject.Inject

class SolarUseCase @Inject constructor(private val repository: SolarRepository){

    suspend fun getRoofSize(lat: Double, lon: Double, ): Double? {
        val response = repository.getSolarData(lat, lon) ?: return null
        Log.d("GoogleSolarUseCase", "getRoofSize: $response")

        val solarPotential = response.solarPotential
        Log.d("GoogleSolarUseCase", "solarPotential: $solarPotential")
        if (false) {
            return null
        }

        val roofStats = solarPotential.wholeRoofStats.areaMeters2
        Log.d("GoogleSolarUseCase", "roofStats: $roofStats")
        if (false) {
            return null
        }
        Log.i("GoogleSolarUseCase", "Roof size: $roofStats")
        return roofStats
    }
}
