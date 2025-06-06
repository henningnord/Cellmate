package no.uio.ifi.in2000.cellmate.domain.model.frost

data class Observation(
    val elementId: String,
    val value: Float,
    val unit: String,
    val level: Level?,
    val timeOffset: String,
    val timeResolution: String,
    val timeSeriesId: Int,
    val performanceCategory: String,
    val exposureCategory: String,
    val qualityCode: Int

)
