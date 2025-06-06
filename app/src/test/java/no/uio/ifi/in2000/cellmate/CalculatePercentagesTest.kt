package no.uio.ifi.in2000.cellmate

import no.uio.ifi.in2000.cellmate.domain.model.SolarPanel
import no.uio.ifi.in2000.cellmate.domain.usecase.calculateInfluxPercentage
import no.uio.ifi.in2000.cellmate.domain.usecase.calculateSolarPanelEfficiencyPercentage
import org.junit.Assert.*
import org.junit.Test

class InfluxEfficiencyTest {

    private val defaultPanel = SolarPanel(
        name = "TestPanel",
        size = 1.6,
        effect = 320,
        price = 3000,
        effectGuarantee = 25
    )

    private val roofAngle = 40

    @Test
    fun testCalculateInfluxPercentage_OnlySnowPenalty() {
        val snow = List(12) { 0.5 }
        val temp = List(12) { 15.0 }
        val influx = List(12) { 500.0 }

        // Expected: 100 - (0.5 * 0.7) = 99.65 → 99
        val result = calculateInfluxPercentage(influx, snow, temp)
        assertEquals(99, result)
    }

    @Test
    fun testCalculateInfluxPercentage_WithSnowAndTempPenalty() {
        val snow = List(12) { 0.5 }
        val temp = List(12) { 0.0 }
        val influx = List(12) { 500.0 }

        // Snow penalty = 0.35, temp penalty = 8.0 → 91.65 → 91
        val result = calculateInfluxPercentage(influx, snow, temp)
        assertEquals(91, result)
    }

    @Test
    fun testCalculateInfluxPercentage_EmptyInput() {
        val result = calculateInfluxPercentage(emptyList(), emptyList(), emptyList())
        assertEquals(0, result)
    }

    @Test
    fun testCalculateSolarPanelEfficiencyPercentage_OptimalConditions() {
            val influx = List(12) { 1000.0 }
        val temp = List(12) { 25.0 }
        val snow = List(12) { 0.0 }

        val result = calculateSolarPanelEfficiencyPercentage(defaultPanel, influx, temp, snow, roofAngle)
        assertEquals(100, result)
    }

    @Test
    fun testCalculateSolarPanelEfficiencyPercentage_ColdAndSnowy() {
        val influx = List(12) { 1000.0 }
        val temp = List(12) { 0.0 }
        val snow = List(12) { 0.5 }

        val result = calculateSolarPanelEfficiencyPercentage(defaultPanel, influx, temp, snow, roofAngle)
        assertEquals(50.0, result.toDouble(), 5.0)    }

    @Test
    fun testCalculateSolarPanelEfficiencyPercentage_FullSnow() {
        val influx = List(12) { 1000.0 }
        val temp = List(12) { 25.0 }
        val snow = List(12) { 1.0 }

        val result = calculateSolarPanelEfficiencyPercentage(defaultPanel, influx, temp, snow, roofAngle)
        assertEquals(0, result)
    }

    @Test
    fun testCalculateSolarPanelEfficiencyPercentage_EmptyInput() {
        val result = calculateSolarPanelEfficiencyPercentage(defaultPanel, emptyList(), emptyList(), emptyList(), roofAngle)
        assertEquals(0, result)
    }
}