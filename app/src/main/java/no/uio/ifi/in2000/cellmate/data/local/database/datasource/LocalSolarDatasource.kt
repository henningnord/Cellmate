package no.uio.ifi.in2000.cellmate.data.local.database.datasource

import android.util.Log
import no.uio.ifi.in2000.cellmate.data.local.database.dao.SolarDao
import no.uio.ifi.in2000.cellmate.data.local.database.entity.SolarCacheEntity
import javax.inject.Inject

class LocalSolarDataSource @Inject constructor(
    private val dao: SolarDao
) {
    suspend fun getCachedRoofSize(lat: Double, lon: Double): SolarCacheEntity? {
        Log.d("Gets cached solar", "getCachedRoofSize: lat: $lat, lon: $lon")
        return dao.getCachedRoofSize(lat, lon)
    }

    suspend fun saveRoofSize(lat: Double, lon: Double, roofSize: Double) {
        Log.d("Cached solar", "saveRoofSize: lat: $lat, lon: $lon")
        val cache = SolarCacheEntity(
            latitude = lat,
            longitude = lon,
            roofSize = roofSize
        )
        dao.insertSolarCache(cache)
    }
}