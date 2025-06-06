package no.uio.ifi.in2000.cellmate.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.cellmate.data.local.database.entity.SavedHomeEntity

@Dao
interface SavedHomeDao {
    @Query("SELECT * FROM saved_homes ORDER BY timestamp DESC LIMIT 3")
    suspend fun getRecentHomes(): List<SavedHomeEntity>

    @Query("SELECT * FROM saved_homes WHERE address = :address LIMIT 1")
    suspend fun getHomeByAddress(address: String): SavedHomeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(home: SavedHomeEntity): Long

    @Delete
    suspend fun delete(home: SavedHomeEntity)
}