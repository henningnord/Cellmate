package no.uio.ifi.in2000.cellmate.data.repositoryImpl

import jakarta.inject.Inject
import no.uio.ifi.in2000.cellmate.data.local.database.dao.SavedHomeDao
import no.uio.ifi.in2000.cellmate.data.local.database.entity.SavedHomeEntity
import no.uio.ifi.in2000.cellmate.domain.repository.SavedHomeRepository

class SavedHomeRepositoryImpl @Inject constructor(
    private val savedHomeDao: SavedHomeDao
) : SavedHomeRepository {

    override suspend fun getRecentHomes(): List<SavedHomeEntity> {
        return savedHomeDao.getRecentHomes()
    }

    override suspend fun saveHome(address: String, latitude: Double, longitude: Double): Long {

        val existingHome = savedHomeDao.getHomeByAddress(address)
        if (existingHome != null) {
            // Updates timestamp for existing home
            val updatedHome = existingHome.copy(timestamp = System.currentTimeMillis())
            return savedHomeDao.insert(updatedHome)
        }
        // Creates new home
        val home = SavedHomeEntity(
            address = address,
            latitude = latitude,
            longitude = longitude,
            timestamp = System.currentTimeMillis()
        )
        return savedHomeDao.insert(home)
    }

    override suspend fun deleteHome(home: SavedHomeEntity) {
        savedHomeDao.delete(home)
    }
}