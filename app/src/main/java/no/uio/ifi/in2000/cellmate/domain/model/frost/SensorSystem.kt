package no.uio.ifi.in2000.cellmate.domain.model.frost

import com.google.gson.annotations.SerializedName

data class SensorSystem(
    @SerializedName("@type") val type: String,
    val id: String,
    val name: String,
    val shortName: String,
    val country: String,
    val countryCode: String,
    val geometry: Geometry,
    val distance: Double,
    val masl: Int,
    val validFrom: String,
    val county: String,
    val countyId: Int,
    val municipality: String,
    val municipalityId: Int,
    val ontologyId: Int,
    val stationHolders: List<String>,
    val externalIds: List<String>,
    val wigosId: String
)