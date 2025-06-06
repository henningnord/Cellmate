package no.uio.ifi.in2000.cellmate.data.repositoryImpl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.cellmate.domain.repository.PriceRepository
import javax.inject.Inject

class PriceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PriceRepository {
    private val json = Json { ignoreUnknownKeys = true }

    override fun getMonthlyPrices(): Map<String, List<Double>> {
        val jsonString = context.assets.open("powerprices.json")
            .bufferedReader()
            .use { it.readText() }

        return json.decodeFromString(jsonString)
    }
}