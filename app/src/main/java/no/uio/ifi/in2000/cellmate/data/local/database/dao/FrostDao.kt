package no.uio.ifi.in2000.cellmate.data.local.database.dao

import androidx.room.*
import no.uio.ifi.in2000.cellmate.data.local.database.entity.FrostObservationDataEntity
import no.uio.ifi.in2000.cellmate.data.local.database.entity.FrostCacheEntity
import no.uio.ifi.in2000.cellmate.data.local.database.entity.FrostObservationEntity
import no.uio.ifi.in2000.cellmate.domain.model.frost.ObservationResponse

data class ObservationWithData(
    @Embedded val observation: FrostObservationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "observationId"
    )
    val data: List<FrostObservationDataEntity>
)

data class CacheWithData(
    @Embedded val cache: FrostCacheEntity,
    @Relation(
        entity = FrostObservationEntity::class,
        parentColumn = "id",
        entityColumn = "cacheId"
    )
    val observations: List<ObservationWithData>
)

@Dao
interface FrostDao {
    @Transaction
    @Query("SELECT * FROM frost_cache WHERE latitude = :lat AND longitude = :lon AND timestamp > :minTimestamp LIMIT 1")
    suspend fun getFrostData(lat: Double, lon: Double, minTimestamp: Long): CacheWithData?

    @Insert
    suspend fun insertCache(cache: FrostCacheEntity): Long

    @Insert
    suspend fun insertObservation(observation: FrostObservationEntity): Long

    @Insert
    suspend fun insertObservationData(data: List<FrostObservationDataEntity>)

    @Query("DELETE FROM frost_cache WHERE timestamp < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)

    @Transaction
    @Query("SELECT * FROM frost_cache WHERE latitude = :lat AND longitude = :lon AND (:elements IS NULL OR elements = :elements) AND timestamp >= :minTimestamp LIMIT 1")
    suspend fun getFrostData(lat: Double, lon: Double, minTimestamp: Long, elements: String? = null): CacheWithData?


    @Transaction
    suspend fun insertFrostData(cache: FrostCacheEntity, observationResponse: ObservationResponse) {
        val cacheId = insertCache(cache)

        observationResponse.data.forEach { dataPoint ->
            val observation = FrostObservationEntity(
                cacheId = cacheId,
                sourceId = dataPoint.sourceId,
                referenceTime = dataPoint.referenceTime
            )
            val observationId = insertObservation(observation)

            val observationData = dataPoint.observations?.map { obs ->
                FrostObservationDataEntity(
                    observationId = observationId,
                    elementId = obs.elementId ,
                    value = obs.value.toDouble(),
                    unit = obs.unit,
                    level = obs.level?.toString()
                )
            } ?: emptyList()

            insertObservationData(observationData)
        }
    }
}