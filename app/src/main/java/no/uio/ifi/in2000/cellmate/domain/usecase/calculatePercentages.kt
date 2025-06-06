package no.uio.ifi.in2000.cellmate.domain.usecase

import no.uio.ifi.in2000.cellmate.domain.model.SolarPanel
import java.util.Calendar

fun calculateInfluxPercentage(
    monthlyInflux: List<Double>,
    monthlySnow: List<Double>,
    monthlyTemp: List<Double>
): Int {
    if (monthlyInflux.isEmpty() || monthlySnow.isEmpty() || monthlyTemp.isEmpty()) return 0

    val currentMonth = Calendar.getInstance().get(Calendar.MONTH)

    val snowCover = monthlySnow[currentMonth]
    val temperature = monthlyTemp[currentMonth]

    var influx = 100.0

    influx -= (snowCover * 0.7)

    if (temperature < 10) {
        val tempPenalty = (10 - temperature) * 0.8
        influx -= tempPenalty
    }

    influx = influx.coerceIn(0.0, 100.0) //keeping percentage between 0-100

    return influx.toInt()
}

fun calculateSolarPanelEfficiencyPercentage(
    panelType: SolarPanel?,
    monthlyInflux: List<Double>,
    monthlyTemp: List<Double>,
    monthlySnow: List<Double>,
    roofAngle: Int
): Int {
    if (monthlyInflux.isEmpty() || monthlyTemp.isEmpty() || monthlySnow.isEmpty()) return 0

    val currentMonth = Calendar.getInstance().get(Calendar.MONTH)

    val influx = monthlyInflux[currentMonth]
    val temperature = monthlyTemp[currentMonth]
    val snowCover = monthlySnow[currentMonth]

    val actualProduction = calculateProduction(panelType, influx, temperature, snowCover, roofAngle)
    val idealProduction = calculateProduction(panelType, influx, 25.0, 0.0, roofAngle)

    if (idealProduction == 0.0) return 0

    val efficiencyPercentage = (actualProduction / idealProduction) * 100.0

    return efficiencyPercentage.toInt().coerceIn(0, 100)
}