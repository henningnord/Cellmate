package no.uio.ifi.in2000.cellmate.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "frost_observation",
    foreignKeys = [
        ForeignKey(
            entity = FrostCacheEntity::class,
            parentColumns = ["id"],
            childColumns = ["cacheId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cacheId")]
)
data class FrostObservationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cacheId: Long,
    val sourceId: String,
    val referenceTime: String
)