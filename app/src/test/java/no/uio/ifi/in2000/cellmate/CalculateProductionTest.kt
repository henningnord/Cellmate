package no.uio.ifi.in2000.cellmate

import no.uio.ifi.in2000.cellmate.domain.model.SolarPanel
import no.uio.ifi.in2000.cellmate.domain.usecase.calculateProduction
import org.junit.Assert.*
import org.junit.Test

class CalculateProductionTest {

    private val testPanel = SolarPanel(
        name = "TestPanel",
        size = 1.6,         // m²
        effect = 320,       // W
        price = 2000,       // NOK
        effectGuarantee = 25
    )

    @Test
    fun testCalculateProduction_NormalConditions() {
        val influx = 1000.0
        val temperature = 20.0
        val snowCover = 0.0
        val roofAngle = 40

        val result = calculateProduction(testPanel, influx, temperature, snowCover, roofAngle)

        assertTrue(result > 300.0 && result < 315.0)    }

    @Test
    fun testCalculateProduction_HighTemperature() {
        val influx = 1000.0
        val snowCover = 0.0
        val roofAngle = 40

        val resultHighTemp = calculateProduction(testPanel, influx, 45.0, snowCover, roofAngle)
        val resultBaseline = calculateProduction(testPanel, influx, 25.0, snowCover, roofAngle)

        assertTrue(resultHighTemp < resultBaseline)
    }

    @Test
    fun testCalculateProduction_FullSnowCover() {
        val influx = 1000.0
        val temperature = 10.0
        val snowCover = 1.0
        val roofAngle = 40

        val result = calculateProduction(testPanel, influx, temperature, snowCover, roofAngle)

        assertEquals(0.0, result, 0.01)
    }

    @Test
    fun testCalculateProduction_ClampedTemperature() {
        val influx = 1000.0
        val snowCover = 0.0
        val roofAngle = 40

        val result = calculateProduction(testPanel, influx, -100.0, snowCover, roofAngle)
        val expected = calculateProduction(testPanel, influx, -30.0, snowCover, roofAngle)

        assertEquals(expected, result, 0.01)
    }

    @Test
    fun testCalculateProduction_InvalidPanel() {
        val influx = 1000.0
        val temperature = 20.0
        val snowCover = 0.0
        val roofAngle = 40

        val result = calculateProduction(null, influx, temperature, snowCover, roofAngle)

        assertEquals(0.0, result, 0.0)
    }
}