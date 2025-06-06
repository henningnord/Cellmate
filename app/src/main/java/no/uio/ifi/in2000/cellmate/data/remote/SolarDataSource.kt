package no.uio.ifi.in2000.cellmate.data.remote

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.uio.ifi.in2000.cellmate.data.clientprovider.HttpClientProvider
import no.uio.ifi.in2000.cellmate.domain.model.solar.SolarResponse
import javax.inject.Inject

class SolarDataSource @Inject constructor() {

    private val client = HttpClientProvider.client
    private val apiKey = ""
    private val baseUrl = "https://solar.googleapis.com/v1/buildingInsights:findClosest"

    suspend fun fetchSolarInfo(
        lat: Double,
        lon: Double,
    ): SolarResponse? {
        return try {
            val response = client.get(baseUrl) {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                parameter("location.latitude", lat)
                parameter("location.longitude", lon)
                parameter("requiredQuality", "MEDIUM")
                parameter("key", apiKey)
            }

            response.body<SolarResponse>()
        } catch (e: Exception) {
            Log.d("SolarDataSource", "Error fetching solar data: ${e.message}")
            null
        }
    }
}