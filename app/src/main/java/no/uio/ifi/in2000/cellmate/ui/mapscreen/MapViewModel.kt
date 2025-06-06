package no.uio.ifi.in2000.cellmate.ui.mapscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.search.autocomplete.PlaceAutocompleteResult
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.cellmate.domain.usecase.MapboxUseCase
import javax.inject.Inject
import com.mapbox.geojson.Point

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapboxUseCase: MapboxUseCase,

) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<PlaceAutocompleteSuggestion>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _selectedPlace = MutableStateFlow<PlaceAutocompleteResult?>(null)
    val selectedPlace = _selectedPlace.asStateFlow()

    private val _selectedAddress = MutableStateFlow<String?>(null)
    val selectedAddress: StateFlow<String?> = _selectedAddress.asStateFlow()

    private val _postalCode = MutableStateFlow<String?>(null)
    val postalCode: StateFlow<String?> = _postalCode.asStateFlow()

    private val _coordinates = MutableStateFlow<Pair<Double, Double>?>(null)
    val coordinates: StateFlow<Pair<Double, Double>?> = _coordinates.asStateFlow()

    private val _pinLocation = MutableStateFlow<Point?>(null)
    val pinLocation = _pinLocation.asStateFlow()

    private val _newLocation = MutableStateFlow<Boolean>(false)
    val newLocation = _newLocation.asStateFlow()


    fun searchLocations(query: String) {
        viewModelScope.launch {
            try {
                val suggestions = mapboxUseCase.searchLocation(query)
                _searchResults.value = suggestions ?: emptyList()
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error fetching suggestions", e)
            }
        }
    }

    fun setPinLocation(point: Point) {
        _pinLocation.value = point
    }


    fun selectSuggestion(suggestion: PlaceAutocompleteSuggestion) {
        viewModelScope.launch {
            try {
                val result = mapboxUseCase.pickSuggestion(suggestion)
                _selectedPlace.value = result
                _postalCode.value = result?.address?.postcode

                result?.coordinate?.let { coordinate ->
                    val point = Point.fromLngLat(coordinate.longitude(), coordinate.latitude())
                    _coordinates.value = Pair(coordinate.longitude(), coordinate.latitude())
                    setPinLocation(point)
                }

                val fullAddress = suggestion.formattedAddress ?: suggestion.name
                _selectedAddress.value = fullAddress

                Log.d("MapViewModel", "Selected place: $fullAddress, Coordinates: ${result?.coordinate}")
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error selecting suggestion", e)
            }
        }
    }

    fun  setPostalCode(postcode: String?) {
        if(postcode == null){
            _postalCode.value = "0356" // Default value if postcode is null
        } else {
            _postalCode.value = postcode
        }
    }

    fun setAddressWithCoordinates(address: String, longitude: Double, latitude: Double) {
        _selectedAddress.value = address
        _coordinates.value = Pair(longitude, latitude)
        _pinLocation.value = Point.fromLngLat(longitude, latitude)

        Log.d("MapViewModel", "Setting address with coordinates: $address at ($latitude, $longitude)")
    }
    fun selectAddress(address: String?) {
        viewModelScope.launch {
            try {
                val suggestions = mapboxUseCase.searchLocation(address.toString())
                if (suggestions?.isNotEmpty() == true) {
                    val selectedSuggestion = suggestions[0]
                    Log.i("MapViewModel", "Selected suggestion: ${selectedSuggestion.name}")
                    val result = mapboxUseCase.pickSuggestion(selectedSuggestion)

                    _selectedAddress.value = selectedSuggestion.formattedAddress ?: selectedSuggestion.name

                    _postalCode.value = result?.address?.postcode
                    _coordinates.value = result?.coordinate?.let { Pair(it.longitude(), it.latitude()) }

                    Log.d("MapViewModel", "Address details: Address=${_selectedAddress.value}, Postcode=${result?.address?.postcode}, Coordinates=${result?.coordinate}")
                } else {
                    _selectedAddress.value = address // Fallback to the original address if no suggestions is found
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error getting address details", e)
            }
        }
    }

    fun updateSelectedAddress(address: String, longitude: Double, latitude: Double) {
        _selectedAddress.value = address
        _coordinates.value = Pair(longitude, latitude)
    }

    fun setSelectedAddress(address: String?) {
        _selectedAddress.value = address
    }

    fun setLocation(){
        _newLocation.value = !_newLocation.value
    }


}