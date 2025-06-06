package no.uio.ifi.in2000.cellmate.domain.model.frost


data class ObservationData(
    val sourceId: String,
    val referenceTime: String,
    val observations: List<Observation>

)
