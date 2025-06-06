package no.uio.ifi.in2000.cellmate.domain.model.solar

data class SolarResponse(
    val name: String,
    val center: LatLng,
    val imageryDate: ImageryDate,
    val regionCode: String,
    val solarPotential: SolarPotential
)