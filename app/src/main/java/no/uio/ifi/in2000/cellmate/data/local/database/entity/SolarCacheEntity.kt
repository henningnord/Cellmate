package no.uio.ifi.in2000.cellmate.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solar_cache")
data class SolarCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val roofSize: Double,
    val timestamp: Long = System.currentTimeMillis()
)

