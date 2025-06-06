package no.uio.ifi.in2000.cellmate.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "frost_observation_data",
    foreignKeys = [
        ForeignKey(
            entity = FrostObservationEntity::class,
            parentColumns = ["id"],
            childColumns = ["observationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("observationId")]
)
data class FrostObservationDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val observationId: Long,
    val elementId: String,
    val value: Double,
    val unit: String,
    val level: String? = null
)