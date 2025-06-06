package no.uio.ifi.in2000.cellmate.domain.usecase

import jakarta.inject.Inject
import no.uio.ifi.in2000.cellmate.domain.repository.SavedHomeRepository

class SavedHomeUseCase @Inject constructor(
    private val repository: SavedHomeRepository
) {
    suspend fun getRecentHomes() = repository.getRecentHomes()

    suspend fun saveCurrentHome(address: String, latitude: Double, longitude: Double) =
        repository.saveHome(address, latitude, longitude)

}