package no.uio.ifi.in2000.cellmate.data.remote

import android.content.Context
import android.util.Log
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.common.MapboxOptions
import com.mapbox.search.autocomplete.PlaceAutocompleteResult
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import dagger.hilt.android.qualifiers.ApplicationContext
import no.uio.ifi.in2000.cellmate.R
import javax.inject.Inject

class MapboxSearchDataSource @Inject constructor( @ApplicationContext private val context: Context
) {
    private val placeAutocomplete = PlaceAutocomplete.create(locationProvider = null)
    private var currentSuggestions = mutableListOf<PlaceAutocompleteSuggestion>()

    init {
        MapboxOptions.accessToken = context.getString(R.string.mapbox_access_token)
    }

    suspend fun searchLocation(query: String): List<PlaceAutocompleteSuggestion>? {
        val response = placeAutocomplete.suggestions(query)
        return if (response.isValue) {
            response.value?.also { suggestions ->
                currentSuggestions = suggestions.toMutableList()
                Log.i("SearchExample", "Suggestions: $suggestions")
            }
        } else {
            Log.e("SearchExample", "Error fetching suggestions: ${response.error}")
            emptyList()
        }
    }
    suspend fun pickSuggestion(suggestion: PlaceAutocompleteSuggestion): PlaceAutocompleteResult? {
        val result = placeAutocomplete.select(suggestion)
        return when {
            result.isValue -> {
                Log.i("SearchExample", "Selected result: ${result.value}")
                result.value
            }
            else -> {
                Log.e("SearchExample", "Error selecting suggestion", result.error)
                null
            }
        }
    }

}