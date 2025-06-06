package no.uio.ifi.in2000.cellmate.domain.model.frost

import com.google.gson.annotations.SerializedName

data class Geometry(
    @SerializedName("@type") val type: String,
    val coordinates: List<Double>,
    val nearest: Boolean
)