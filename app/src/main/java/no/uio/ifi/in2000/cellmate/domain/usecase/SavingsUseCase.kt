package no.uio.ifi.in2000.cellmate.domain.usecase

import android.util.Log
import jakarta.inject.Inject
import no.uio.ifi.in2000.cellmate.data.repositoryImpl.PriceRepositoryImpl

class SavingsUseCase @Inject constructor(
    repository: PriceRepositoryImpl,
) {
    val monthlyPrices = repository.getMonthlyPrices()

    fun calculateMonthlySavings(
        electricity: List<Double>, // kWh
        zoneId: String
    ): List<Double> {

        val zone = "NO$zoneId"

        val prices = monthlyPrices[zone] ?: return emptyList()
        Log.d("SavingsUseCase", "Prices for zone $zone: $prices")

        return electricity.zip(prices) { kWh, orePerKWh ->
            val pricePerKWhInKr = orePerKWh / 100.0
            val adjustedPricePerKWh = convertToFinalPrice(pricePerKWhInKr, zone)
            kWh * adjustedPricePerKWh
        }
    }

    private fun convertToFinalPrice(prePrice: Double, zone: String): Double {
        val deductionThreshold = 0.75
        val deductionRate = 0.9

        val deduction = if (prePrice > deductionThreshold) {
            (prePrice - deductionThreshold) * deductionRate
        } else {
            0.0
        }

        val priceAfterSupport = prePrice - deduction

        return if (zone == "NO5") priceAfterSupport else priceAfterSupport * 1.25
    }
}


