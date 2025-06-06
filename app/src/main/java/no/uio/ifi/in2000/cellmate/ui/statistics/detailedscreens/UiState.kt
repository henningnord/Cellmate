package no.uio.ifi.in2000.cellmate.ui.statistics.detailedscreens

import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer

data class UiState(
    val chartModelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val averageY: Float = 0f,
    val minY: Double = 0.0,
    val maxY: Double = 100.0,
    val panelCount: Float = 10f
)

enum class UiStateType {
    POWER, SAVED
}