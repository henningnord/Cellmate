package no.uio.ifi.in2000.cellmate.domain.usecase

import no.uio.ifi.in2000.cellmate.domain.model.SolarPanel

//Calculates production PER PANEL
fun calculateProduction(
    panelType: SolarPanel?,
    influx: Double,
    temperature: Double,
    snowCover: Double,
    roofAngle: Int
): Double {
    val panelSize = panelType?.size ?: return 0.0

    val tiltFactor = when (roofAngle) {
        0 -> 0.85
        40 -> 1.0
        else -> 0.85
    }
    val withOrWithoutTiltAngle = influx * tiltFactor

    val adjustedInflux = withOrWithoutTiltAngle * panelSize

    val validTemperature = temperature.coerceIn(-30.0, 50.0) //a realistic temperature range

    val efficiency = panelType.efficiency
    val temperatureFactor = 1.0 - (validTemperature - 25).coerceIn(-20.0, 20.0) * 0.004 // temp coefficient
    val snowFactor = 1.0 - snowCover
    val inverterEfficiency = 0.96  // 96% efficient inverter
    val wiringLosses = 0.98  // 2% wiring losses

    // Calculate production with losses and system efficiency
    val rawProduction = adjustedInflux *  efficiency
    val adjustedProduction = rawProduction * temperatureFactor * snowFactor * inverterEfficiency * wiringLosses

    return adjustedProduction
}