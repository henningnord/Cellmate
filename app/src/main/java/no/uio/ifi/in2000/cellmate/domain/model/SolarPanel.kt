package no.uio.ifi.in2000.cellmate.domain.model

data class SolarPanel(
    val name: String,
    val size: Double,    // in square meters
    val effect: Int,  // in watts
    val price: Int,       // in NOK
    val effectGuarantee: Int // 20 or 30 years
) {
    val efficiency: Double = effect / (1000 * size)

    override fun toString(): String {
        return "SolarPanel(name='$name', size=${size} m², effect=${effect}W, efficiency=${"%.2f".format(efficiency * 100)}%, price=${price} kr)"
    }
}