package no.uio.ifi.in2000.cellmate.domain.model.solar

data class SolarPotential(
    val maxArrayPanelsCount: Int,
    val maxArrayAreaMeters2: Double,
    val maxSunshineHoursPerYear: Double,
    val carbonOffsetFactorKgPerMwh: Double,
    val wholeRoofStats: RoofStats,
    val roofSegmentStats: List<RoofSegmentStats>
)

