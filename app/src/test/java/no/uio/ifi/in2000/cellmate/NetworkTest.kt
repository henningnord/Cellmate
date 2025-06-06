package no.uio.ifi.in2000.cellmate

import no.uio.ifi.in2000.cellmate.domain.usecase.SimpleNetworkChecker
import no.uio.ifi.in2000.cellmate.domain.usecase.NetworkStatus
import org.junit.Assert.*
import org.junit.Test

class SimpleNetworkCheckerTest {

    private val checker = SimpleNetworkChecker()

    @Test
    fun testOnline_WhenConnected() {
        val result = checker.isOnline(activeNetworkExists = true, hasInternetCapability = true)
        assertEquals(NetworkStatus.Available, result)
    }

    @Test
    fun testOffline_NoNetwork() {
        val result = checker.isOnline(activeNetworkExists = false, hasInternetCapability = true)
        assertEquals(NetworkStatus.Lost, result)
    }

    @Test
    fun testOffline_NoInternet() {
        val result = checker.isOnline(activeNetworkExists = true, hasInternetCapability = false)
        assertEquals(NetworkStatus.Lost, result)
    }

    @Test
    fun testOffline_NonePresent() {
        val result = checker.isOnline(activeNetworkExists = false, hasInternetCapability = false)
        assertEquals(NetworkStatus.Lost, result)
    }
}