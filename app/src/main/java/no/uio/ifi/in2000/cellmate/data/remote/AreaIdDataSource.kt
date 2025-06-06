package no.uio.ifi.in2000.cellmate.data.remote

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import no.uio.ifi.in2000.cellmate.data.clientprovider.HttpClientProvider
import no.uio.ifi.in2000.cellmate.domain.model.hks.priceAreaId
import javax.inject.Inject

class AreaIdDataSource @Inject constructor() {
    private val client = HttpClientProvider.client
    private val TAG = "AreaIdDataSource"

    suspend fun fetchPriceAreaId(postalCode : String): priceAreaId? {
        val baseUrl = "https://www.astrom.no/postalcode/$postalCode"
        Log.i(TAG, "Fetching data from: $baseUrl")
        return try {
            val response = client.get(baseUrl)
            when (response.status) {
                HttpStatusCode.OK -> {
                    response.body<priceAreaId>()
                }
                else -> {
                    Log.e(TAG, "Error fetching data from  $baseUrl: ${response.status}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception occurred while fetching data", e)
            null
        }
    }
}