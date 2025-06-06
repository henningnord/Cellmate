package no.uio.ifi.in2000.cellmate.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.cellmate.data.local.database.entity.ExpectedUsageEntity

@Dao
interface ExpectedUsageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpectedUsage(usage: ExpectedUsageEntity)

    @Query("SELECT * FROM expected_usage WHERE latitude = :lat AND longitude = :lon AND timestamp >= :minTimestamp LIMIT 1")
    suspend fun getExpectedUsage(lat: Double, lon: Double, minTimestamp: Long): ExpectedUsageEntity?
}