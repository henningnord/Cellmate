package no.uio.ifi.in2000.cellmate.domain.repository

import no.uio.ifi.in2000.cellmate.data.local.database.entity.SavedHomeEntity

interface SavedHomeRepository {
    suspend fun getRecentHomes(): List<SavedHomeEntity>
    suspend fun saveHome(address: String, latitude: Double, longitude: Double): Long
    suspend fun deleteHome(home: SavedHomeEntity)
}