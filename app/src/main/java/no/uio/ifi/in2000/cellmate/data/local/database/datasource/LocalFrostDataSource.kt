package no.uio.ifi.in2000.cellmate.data.local.database.datasource

import android.util.Log
import no.uio.ifi.in2000.cellmate.data.local.database.dao.FrostDao
import no.uio.ifi.in2000.cellmate.data.local.database.entity.FrostCacheEntity
import no.uio.ifi.in2000.cellmate.domain.model.frost.ObservationResponse
import no.uio.ifi.in2000.cellmate.domain.model.frost.ObservationData
import no.uio.ifi.in2000.cellmate.domain.model.frost.Observation
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocalFrostDataSource @Inject constructor(
    private val frostDao: FrostDao
) {
    suspend fun getWeatherData(lat: Double, lon: Double, elements: String? = null): ObservationResponse? {
        val minTimestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24)
        // Passes the timestamp to the DAO to get the data
        val cachedData = frostDao.getFrostData(lat, lon, minTimestamp, elements)
        Log.d("Cached data", "$cachedData")
        Log.d("Cached data", "$lat $lon $elements")

        return cachedData?.let { cache ->
            ObservationResponse(
                context = "",
                type = "",
                apiVersion = "",
                license = "",
                createdAt = "",
                queryTime = 0.0f,
                currentItemCount = 0,
                itemsPerPage = 0,
                offset = 0,
                totalItemCount = 0,
                currentLink = "",
                data = cache.observations.map { obs ->
                    ObservationData(
                        sourceId = obs.observation.sourceId,
                        referenceTime = obs.observation.referenceTime,
                        observations = obs.data.map { data ->
                            Observation(
                                elementId = data.elementId,
                                value = data.value.toFloat(),
                                unit = data.unit,
                                level = null,
                                timeOffset = "",
                                timeResolution = "",
                                timeSeriesId = 0,
                                performanceCategory = "",
                                exposureCategory = "",
                                qualityCode = 0
                            )
                        }
                    )
                }
            )
        }
    }

    suspend fun saveWeatherData(lat: Double, lon: Double, elements: String, weatherData: ObservationResponse) {
        Log.d("Saving data in cache", "$lat $lon $elements")
        val cache = FrostCacheEntity(
            latitude = lat,
            longitude = lon,
            elements = elements
        )
        frostDao.insertFrostData(cache, weatherData)
    }

}