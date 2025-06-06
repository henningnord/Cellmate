package no.uio.ifi.in2000.cellmate.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import no.uio.ifi.in2000.cellmate.data.local.database.entity.SolarCacheEntity

@Dao
interface SolarDao {
    @Query("SELECT * FROM solar_cache WHERE latitude = :lat AND longitude = :lon ORDER BY timestamp DESC LIMIT 1")
    suspend fun getCachedRoofSize(lat: Double, lon: Double): SolarCacheEntity?

    @Insert
    suspend fun insertSolarCache(cache: SolarCacheEntity)
}

