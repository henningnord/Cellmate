package no.uio.ifi.in2000.cellmate

import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.cellmate.data.remote.SolarDataSource
import org.junit.Assert.*
import org.junit.Test
import org.junit.Before

class SolarApiTest {

    private lateinit var solarDataSource: SolarDataSource

    @Before
    fun setUp() {
        solarDataSource = SolarDataSource()
    }

    @Test
    fun testSolarDataIsNotNull() = runTest {
        val osloLat = 59.9139
        val osloLon = 10.7522
        val solarData = solarDataSource.fetchSolarInfo(osloLat, osloLon)
        assertNotNull("Solar data should not be null", solarData)
    }

    @Test
    fun testSolarDataNameIsNotNull() = runTest {
        val osloLat = 59.9139
        val osloLon = 10.7522
        val solarData = solarDataSource.fetchSolarInfo(osloLat, osloLon)
        assertNotNull("Location name should not be null", solarData?.name)
    }

    @Test
    fun testLatitudeMatchesInput() = runTest {
        val osloLat = 59.9139
        val osloLon = 10.7522
        val solarData = solarDataSource.fetchSolarInfo(osloLat, osloLon)
        assertEquals("Coordinates should match input (latitude)", osloLat, solarData?.center?.latitude ?: 0.0, 0.1)
    }

    @Test
    fun testLongitudeMatchesInput() = runTest {
        val osloLat = 59.9139
        val osloLon = 10.7522
        val solarData = solarDataSource.fetchSolarInfo(osloLat, osloLon)
        assertEquals("Coordinates should match input (longitude)", osloLon, solarData?.center?.longitude ?: 0.0, 0.1)
    }

    @Test
    fun testFetchSolarInfoWithInvalidCoordinates() = runTest {
        val invalidLat = 200.0
        val invalidLon = 500.0

        try {
            val result = solarDataSource.fetchSolarInfo(invalidLat, invalidLon)
            assertNull("Should return null for invalid coordinates", result?.name)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}