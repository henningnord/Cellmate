package no.uio.ifi.in2000.cellmate.domain.repository

import com.mapbox.search.autocomplete.PlaceAutocompleteResult
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion

interface MapBoxRepository {
    suspend fun searchLocation(query: String): List<PlaceAutocompleteSuggestion>?
    suspend fun pickSuggestion(suggestion: PlaceAutocompleteSuggestion): PlaceAutocompleteResult?
}