
package no.uio.ifi.in2000.cellmate.data.repositoryImpl

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.cellmate.data.local.database.datasource.LocalFrostDataSource
import no.uio.ifi.in2000.cellmate.data.remote.FrostDataSource
import no.uio.ifi.in2000.cellmate.domain.repository.FrostRepository
import no.uio.ifi.in2000.cellmate.domain.model.frost.ObservationResponse
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.joinToString


class FrostRepositoryImpl @Inject constructor(
    private val dataSource: FrostDataSource,
    private val localDataSource: LocalFrostDataSource,
    @Named("frost_base_url") private val baseUrl: String,
    @Named("frost_history_days") private val historyDays: Long
) : FrostRepository {

    private fun getCurrentDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(cal.time)
    }

    private fun getXAgoDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -historyDays.toInt())
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(cal.time)
    }

    // This function fetches the first valid station for the given elements and coordinates, assures that the station has data we need
    private suspend fun getFirstValidStation(elements: List<String>, lat: Double, lon: Double): String? {
        val elementsString = URLEncoder.encode(elements.joinToString(","), "UTF-8")
        val point = URLEncoder.encode("POINT($lon $lat)", "UTF-8")
        val url = "$baseUrl/sources/v0.jsonld?" +
                "elements=$elementsString" +
                "&geometry=nearest($point)" +
                "&nearestmaxcount=5"

        val stations = dataSource.getSourceData(url)?.data ?: return null

        stations.forEach {
            val stationId = URLEncoder.encode(it.id, "UTF-8")
            val referencetime = "${getXAgoDate()}T00:00:00Z/${getCurrentDate()}T23:59:59Z"
            val testUrl = "$baseUrl/observations/v0.jsonld?" +
                    "sources=$stationId" +
                    "&referencetime=$referencetime" +
                    "&elements=$elementsString"
            val result = dataSource.fetchObservationData(testUrl)
            if (result?.data?.isNotEmpty() == true) {
                Log.i("FrostRepository", "Using station ${it.id} with available data")
                return it.id
            }
        }
        return null
    }


    private suspend fun generateUrl(elements: List<String>, lat: Double, lon: Double): String {
        val stationId =
            URLEncoder.encode(getFirstValidStation(elements, lat, lon) ?: "18700", "UTF-8")
        Log.d("FrostRepository", "stationId: $stationId")
        val elementsString = URLEncoder.encode(elements.joinToString(","), "UTF-8")
        val referencetime = "${getXAgoDate()}T00:00:00Z/${getCurrentDate()}T23:59:59Z"
        val url = "$baseUrl/observations/v0.jsonld?" +
                "sources=$stationId" +
                "&referencetime=$referencetime" +
                "&elements=$elementsString"
        Log.i("FrostRepository", "Generated URL: $url")
        return url
    }

    // The only function that you need to use, just tune the elements list to get desired data
    override suspend fun getFrostData(elements: List<String>, lat: Double, lon: Double): ObservationResponse? {
        val elementsString = elements.joinToString(",")
        // data in cache?
        localDataSource.getWeatherData(lat, lon, elementsString)?.let {
            Log.d("FrostRepository", "Returning cached data for $elementsString")
                return it
            }
        // If not in cache, fetches from API
        Log.d("FrostRepository", "Fetching data from API")
        val url = generateUrl(elements, lat, lon)
        val result = dataSource.fetchObservationData(url)
        Log.d("FrostRepository", "result: $result")
        if (result != null) {
            Log.d("FrostRepository", "Caching the data")
            // Cache the response with the elements
            localDataSource.saveWeatherData(lat, lon, elementsString, result)
            }
        return result
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {

    @Provides
    @Named("frost_base_url")
    fun provideFrostBaseUrl(): String = "https://frost.met.no"

    @Provides
    @Named("frost_history_days")
    fun provideHistoryDays(): Long = 730L
}
