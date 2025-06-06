package no.uio.ifi.in2000.cellmate.domain.model.solar

data class RoofStats(
    val areaMeters2: Double,
    val sunshineQuantiles: List<Double>,
    val groundAreaMeters2: Double
)
