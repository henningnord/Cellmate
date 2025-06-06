package no.uio.ifi.in2000.cellmate.data.local.database.entity

import androidx.room.Entity

@Entity(
    tableName = "expected_usage",
    primaryKeys = ["latitude", "longitude"]
)
data class ExpectedUsageEntity(
    val latitude: Double,
    val longitude: Double,
    val expectedEnergyUsageYearly: Int?,
    val roofSize: Int?,
    val timestamp: Long = System.currentTimeMillis()
)