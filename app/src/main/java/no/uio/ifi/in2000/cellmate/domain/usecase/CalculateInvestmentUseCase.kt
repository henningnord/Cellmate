package no.uio.ifi.in2000.cellmate.domain.usecase

import kotlin.math.floor

data class InvestmentInput(
    val panelCount: Int,
    val pricePerPanel: Int,
    val effectPerPanel: Int,
    val effectGuarantee: Int,
    val yearlyValuePerPanel: Double
)

data class InvestmentResult(
    val prePrice: Double,
    val kwp: Double,
    val discount: Double,
    val postPrice: Double,
    val totalValue: Double,
    val yearlySaving: Double,
    val paybackYears: Double
)

class CalculateInvestmentUseCase {
    fun calculate(input: InvestmentInput): InvestmentResult {
        val prePrice = (input.panelCount * input.pricePerPanel).toDouble()
        val kwp = floor((input.effectPerPanel * input.panelCount) / 1000.0)
        val rawDiscount = if (kwp >= 20) 32500.0 else 7500.0 + (kwp * 1250.0)
        val discount = minOf(rawDiscount, prePrice)
        val postPrice = prePrice - discount
        val totalValue = input.yearlyValuePerPanel * input.panelCount * input.effectGuarantee
        val yearlySaving = (totalValue - prePrice + discount) / input.effectGuarantee
        val paybackYears = when {
            postPrice <= 0 -> 0.0
            input.yearlyValuePerPanel * input.panelCount <= 0 -> Double.POSITIVE_INFINITY
            else -> postPrice / (input.yearlyValuePerPanel * input.panelCount)
        }

        return InvestmentResult(
            prePrice, kwp, discount, postPrice,
            totalValue, yearlySaving, paybackYears
        )
    }
}