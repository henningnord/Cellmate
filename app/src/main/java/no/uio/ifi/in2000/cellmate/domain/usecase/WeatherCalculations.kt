package no.uio.ifi.in2000.cellmate.domain.usecase

import android.util.Log
import no.uio.ifi.in2000.cellmate.domain.model.frost.ObservationData
import no.uio.ifi.in2000.cellmate.domain.model.frost.ObservationResponse
import no.uio.ifi.in2000.cellmate.domain.repository.FrostRepository
import javax.inject.Inject

class WeatherCalculations @Inject constructor(
    private val repository: FrostRepository
) {
    private suspend fun fetchFrostData(
        lat: Double,
        lon: Double,
        elements: List<String>
    ): ObservationResponse? = repository.getFrostData(elements, lat, lon)

    // Extracts the values from the response data
    private fun extractResponseValues(data: List<ObservationData>): List<Double> {
        return data.flatMap { dataPoint ->
            dataPoint.observations.map { it.value.toDouble() }
        }
    }
    // Returns a number that is in the format kWh/m²
    suspend fun getYearlyInflux(
        lat: Double,
        lon: Double,
    ): List<Double> {
        Log.i("WeatherCalculations", "getYearlyInflux: lat: $lat, lon: $lon")
        val response = fetchFrostData(lat, lon, elements = listOf("mean(surface_downwelling_shortwave_flux_in_air PT1H)")) ?: return emptyList()
        // we just group the data by month
        val monthlyData = response.data.groupBy { dataPoint ->
            dataPoint.referenceTime.substring(5, 7).toInt()
        }
        // Calculate average for each month and sum them together
        return (1..12).map { month ->
            monthlyData[month]?.let { monthData ->
                val monthValues = extractResponseValues(monthData)
                if (monthValues.isNotEmpty()) {
                    val monthlyAverage = monthValues.average()
                    val hoursInMonth = 24 * getDaysInMonth(month)
                    monthlyAverage * hoursInMonth / 1000
                } else null
            } ?: 0.0
        }
    }

    private fun getDaysInMonth(month: Int): Int {
        return when (month) {
            2 -> 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
    }

    suspend fun getYearlyTemp(
        lat: Double,
        lon: Double,
    ): List<Double> {
        Log.i("WeatherCalculations", "getYearlyTemperature: lat: $lat, lon: $lon")
        val response = fetchFrostData(lat, lon, elements = listOf("mean(air_temperature P1D)")) ?: return emptyList()
        // Group data by month
        val monthlyData = response.data.groupBy { dataPoint ->
            dataPoint.referenceTime.substring(5, 7).toInt()
        }
        return (1..12).map { month ->
            monthlyData[month]?.let { monthData ->
                val monthValues = extractResponseValues(monthData)
                if (monthValues.isNotEmpty()) {
                    val rawMonthTemp = monthValues.average()
                    if (rawMonthTemp > 60) rawMonthTemp - 100 else rawMonthTemp
                } else null
            } ?: 0.0
        }
    }

    suspend fun getYearlySnowCoverage(
        lat: Double,
        lon: Double,
    ): List<Double> {
        Log.i("WeatherCalculations", "getYearlySnowCoverage: lat: $lat, lon: $lon")
        val response = fetchFrostData(lat, lon, elements = listOf("mean(snow_coverage_type P1M)"))
        if (response == null) {
            return List(12) { 0.0 }
        }
        val monthlyData = response.data.groupBy { dataPoint ->
            dataPoint.referenceTime.substring(5, 7).toInt()
        }

        return (1..12).map { month ->
            monthlyData[month]?.let { monthData ->
                val monthValues = extractResponseValues(monthData)
                if (monthValues.isNotEmpty()) {
                    val avgSnowValue = monthValues.average()
                    mapSnowToCover(avgSnowValue)
                } else {
                    0.0
                }
            } ?: run {
                0.0
            }
        }
    }

    private fun mapSnowToCover(snowValue: Double): Double {
        return when {
            snowValue <= 0.0 -> 0.0       // No snow
            snowValue <= 1.0 -> 0.05      // Very light snow (5% reduction)
            snowValue <= 2.0 -> 0.15      // Light snow (15% reduction)
            snowValue <= 3.0 -> 0.30      // Moderate snow (30% reduction)
            snowValue <= 4.0 -> 0.50      // Heavy snow (50% reduction)
            else -> 0.60                   // Extreme snow (60% reduction)
        }
    }
}
