package no.uio.ifi.in2000.cellmate.data.repositoryImpl

import com.mapbox.search.autocomplete.PlaceAutocompleteResult
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import no.uio.ifi.in2000.cellmate.data.remote.MapboxSearchDataSource
import no.uio.ifi.in2000.cellmate.domain.repository.MapBoxRepository
import javax.inject.Inject


class MapBoxRepositoryImpl @Inject constructor(private val dataSource: MapboxSearchDataSource) : MapBoxRepository {
    override suspend fun searchLocation(query: String): List<PlaceAutocompleteSuggestion>? {
        return dataSource.searchLocation(query)
    }

    override suspend fun pickSuggestion(suggestion: PlaceAutocompleteSuggestion): PlaceAutocompleteResult? {
        return dataSource.pickSuggestion(suggestion)
    }
}