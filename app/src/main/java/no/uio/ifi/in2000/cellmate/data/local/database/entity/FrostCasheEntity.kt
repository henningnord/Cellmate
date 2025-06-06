package no.uio.ifi.in2000.cellmate.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "frost_cache")
data class FrostCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val elements: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)