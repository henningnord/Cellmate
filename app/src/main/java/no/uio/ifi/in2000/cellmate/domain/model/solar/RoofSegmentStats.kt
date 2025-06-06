package no.uio.ifi.in2000.cellmate.domain.model.solar

data class RoofSegmentStats(
    val pitchDegrees: Double,
    val azimuthDegrees: Double,
    val stats: RoofStats,
    val center: LatLng,
    val boundingBox: BoundingBox,
    val planeHeightAtCenterMeters: Double
)