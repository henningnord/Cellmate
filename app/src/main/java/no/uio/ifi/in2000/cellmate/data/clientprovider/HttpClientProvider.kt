package no.uio.ifi.in2000.cellmate.data.clientprovider

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.gson.gson

// Singleton object for HttpClient (should work for all teh datasource)
object HttpClientProvider {
    val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }
}