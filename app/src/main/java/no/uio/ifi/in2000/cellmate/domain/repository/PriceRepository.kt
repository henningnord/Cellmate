package no.uio.ifi.in2000.cellmate.domain.repository

interface PriceRepository {
    fun getMonthlyPrices(): Map<String, List<Double>>
}