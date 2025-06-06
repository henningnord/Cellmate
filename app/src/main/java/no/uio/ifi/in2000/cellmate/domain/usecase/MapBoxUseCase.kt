package no.uio.ifi.in2000.cellmate.domain.usecase

import com.mapbox.search.autocomplete.PlaceAutocompleteResult
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import no.uio.ifi.in2000.cellmate.data.repositoryImpl.MapBoxRepositoryImpl
import javax.inject.Inject

class MapboxUseCase @Inject constructor(private val mapboxRepository: MapBoxRepositoryImpl) {

    suspend fun searchLocation(query: String): List<PlaceAutocompleteSuggestion>? {
        return mapboxRepository.searchLocation(query)
    }

    suspend fun pickSuggestion(suggestion: PlaceAutocompleteSuggestion): PlaceAutocompleteResult? {
        return mapboxRepository.pickSuggestion(suggestion)
    }
}