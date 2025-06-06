package no.uio.ifi.in2000.cellmate.domain.usecase

import javax.inject.Inject

class GraphCalculationsUseCase @Inject constructor()  {
    fun getAdjustedValues(data: Map<String, Double>, panelCount: Float): List<Float> {
        return data.values.map { (it * panelCount).toFloat() }
    }

    fun calculateAverage(values: List<Float>): Float {
        return if (values.isNotEmpty()) values.average().toFloat() else 0f
    }

    fun generateAverageLine(length: Int, average: Float): List<Float> {
        return List(length) { average }
    }

    fun getXIndices(size: Int): List<Float> {
        return (0 until size).map { it.toFloat() }
    }

    fun calculateYAxisRange(values: List<Double>): Pair<Double, Double> {
        val min = values.minOrNull() ?: 0.0
        val max = values.maxOrNull() ?: 0.0
        return min * 0.8 to max * 1.05
    }
}