package no.uio.ifi.in2000.cellmate.data.remote

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.util.encodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.cellmate.data.clientprovider.HttpClientProvider
import no.uio.ifi.in2000.cellmate.domain.model.frost.ObservationResponse
import no.uio.ifi.in2000.cellmate.domain.model.frost.SourceResponse
import javax.inject.Inject

class FrostDataSource @Inject constructor() {
    private val clientID = ""
    private val clientPassword = ""
    private val client = HttpClientProvider.client

    suspend fun fetchObservationData(baseUrl: String): ObservationResponse? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("FrostDataSource", "Fetching data from API")
            val auth = "Basic " + "$clientID:$clientPassword".encodeToByteArray().encodeBase64()
            val response = client.get(baseUrl) {
                header(HttpHeaders.Authorization, auth)
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }

            when(response.status) {
                HttpStatusCode.OK -> response.body<ObservationResponse>()
                else -> {
                    Log.i("FrostDatasource","Error fetching data: ${response.status}")
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getSourceData(baseUrl: String): SourceResponse? = withContext(Dispatchers.IO) {
        return@withContext try {
            val auth = "Basic " + "$clientID:$clientPassword".encodeToByteArray().encodeBase64()
            val response = client.get(baseUrl) {
                header(HttpHeaders.Authorization, auth)
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
            when (response.status) {
                HttpStatusCode.OK -> response.body<SourceResponse>()
                else -> {
                    Log.i("FrostDatasource", "Error fetching data: ${response.status}")
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}