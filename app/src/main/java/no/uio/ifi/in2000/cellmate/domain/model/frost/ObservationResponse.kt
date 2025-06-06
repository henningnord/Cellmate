package no.uio.ifi.in2000.cellmate.domain.model.frost

import com.google.gson.annotations.SerializedName

data class ObservationResponse(
    @SerializedName("@context") val context: String,
    @SerializedName("@type") val type: String,
    val apiVersion: String,
    val license: String,
    val createdAt: String,
    val queryTime: Float,
    val currentItemCount: Int,
    val itemsPerPage: Int,
    val offset: Int,
    val totalItemCount: Int,
    val currentLink: String,
    val data: List<ObservationData>
)