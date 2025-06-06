package no.uio.ifi.in2000.cellmate.ui.userinput

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.cellmate.data.local.database.entity.SavedHomeEntity
import no.uio.ifi.in2000.cellmate.domain.usecase.ExpectedUsageUseCase
import no.uio.ifi.in2000.cellmate.domain.usecase.SavedHomeUseCase
import no.uio.ifi.in2000.cellmate.domain.usecase.SolarUseCase
import no.uio.ifi.in2000.cellmate.domain.model.SolarPanel
import kotlin.math.roundToInt

@HiltViewModel
class UserInputViewModel @Inject constructor(
    private val expectedUsageUseCase: ExpectedUsageUseCase,
    private val solarUseCase: SolarUseCase,
    private val savedHomeUseCase: SavedHomeUseCase

) : ViewModel() {

    private val _expectedEnergyUsageYearly = MutableStateFlow<Int?>(null)
    val expectedEnergyUsageYearly = _expectedEnergyUsageYearly.asStateFlow()

    private val _roofSize = MutableStateFlow<Int?>(null)
    val roofSize = _roofSize.asStateFlow()

    private val _maxPanels = MutableStateFlow<Float>(100f)
    val maxPanels = _maxPanels.asStateFlow()

    private val _savedHomes = MutableStateFlow<List<SavedHomeEntity>>(emptyList())
    val savedHomes: StateFlow<List<SavedHomeEntity>> = _savedHomes

    private val _currentAddress = MutableStateFlow<String?>(null)

    private val _selectedAddress = MutableStateFlow<String?>(null)

    init {
        loadSavedHomes()
    }

    private val _numberOfPanels = MutableStateFlow<Int>(maxPanels.value.toInt())
    val numberOfPanels = _numberOfPanels.asStateFlow()

    private val _roofAngle = MutableStateFlow<Int?>(null)
    val roofAngle = _roofAngle.asStateFlow()

    private val _isRoofFlat = MutableStateFlow(false)
    val isRoofFlat = _isRoofFlat.asStateFlow()


    private val _lastRoofSizeCoords = MutableStateFlow<Pair<Double, Double>?>(null)
    private val _autoFetchedRoofSize = MutableStateFlow<Int?>(null)
    val autoFetchedRoofSize = _autoFetchedRoofSize.asStateFlow()

    val panelChoices = mapOf<String, SolarPanel>(
        "okonomi" to SolarPanel(
            name = "Økonomi",
            size = 1.755 * 1.04,
            effect = 430,
            price = 4500,
            effectGuarantee = 25
        ),
        "standard" to SolarPanel(
            name = "Standard",
            size = 1.755 * 1.04,
            effect = 450,
            price = 5850,
            effectGuarantee = 25
        ),
        "premium" to SolarPanel(
            name = "Premium",
            size = 1.755 * 1.04,
            effect = 480,
            price = 7200,
            effectGuarantee = 25
        )
    )


    private val _panelChoice = MutableStateFlow<SolarPanel>(panelChoices.getValue("standard"))
    val panelChoice = _panelChoice.asStateFlow()

    fun updateExpectedEnergyUsageYearly(value: Int?) {
        _expectedEnergyUsageYearly.value = value
    }

    fun updateRoofAngle(value: Int) {
        _roofAngle.value = value
    }

    fun updateIsRoofFlat(value: Boolean) {
        _isRoofFlat.value = value
    }

    private fun loadSavedHomes() {
        viewModelScope.launch {
            try {
                _savedHomes.value = savedHomeUseCase.getRecentHomes()
                Log.d("SavedHomes", "Loaded homes: ${_savedHomes.value}")
            } catch (e: Exception) {
                Log.e("SavedHomes", "Failed to load homes", e)
            }
        }
    }

    fun saveCurrentHome(address: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                savedHomeUseCase.saveCurrentHome(address, lat, lon)
                // Reload homes after saving
                loadSavedHomes()
            } catch (e: Exception) {
                Log.e("SavedHomes", "Failed to save home", e)
            }
        }
    }

    fun selectSavedHome(home: SavedHomeEntity) {
        _currentAddress.value = home.address
        _selectedAddress.value = home.address
        // Load all cached data for this location
        loadCachedData(home.latitude, home.longitude)
    }

    fun cacheExpectedUsage(lat: Double, lon: Double) {
        viewModelScope.launch {
            expectedUsageUseCase.cacheUsage(
                lat,
                lon,
                _expectedEnergyUsageYearly.value,
                _roofSize.value
            )
        }
    }

    fun updateRoofSize(value: Int?) {
        val samevalue = value == _roofSize.value
        _roofSize.value = value
        value?.let {
            val max = findMaxPanels(it).toFloat()
            _maxPanels.value = if (max >= 1) max else 1f
            Log.d("MAX", "Max panels updated to $max")
            if (numberOfPanels.value > maxPanels.value) {
                setPanelCount(maxPanels.value)
            }
            else if (!samevalue){
                setPanelCount(maxPanels.value)
            }
        }
    }

    fun setPanelCount(count: Float) {
        _numberOfPanels.value = count.toInt()
    }

    suspend fun findRoofSizeAuto(lat: Double, lon: Double): Int? {
        val solarRoofSize = solarUseCase.getRoofSize(lat, lon)
        return solarRoofSize?.roundToInt()
    }

    fun loadCachedData(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val cachedData = expectedUsageUseCase.getCachedUsage(lat, lon)
                if (cachedData != null) {
                    // Data found - update values
                    _expectedEnergyUsageYearly.value = cachedData.expectedEnergyUsageYearly
                    _roofSize.value = cachedData.roofSize

                    // Update max panels if roof size is available
                    cachedData.roofSize?.let { size ->
                        val max = findMaxPanels(size).toFloat()
                        _maxPanels.value = if (max >= 1) max else 1f
                    }
                } else {
                    // No cached data found - reset values
                    _expectedEnergyUsageYearly.value = null
                    _roofSize.value = null
                }
            } catch (e: Exception) {
                Log.e("UserInputViewModel", "Error loading cached data", e)
                // Reset values on error
                _expectedEnergyUsageYearly.value = null
                _roofSize.value = null
            }
        }
    }

    // panels from solar is set to 1.94 m2
    fun findMaxPanels(roofSize: Int): Double {
        val panel = panelChoice.value ?: return 0.0 // fallback
        return (roofSize * 0.8) / panel.size
    }

    fun setPanelChoice(panel : SolarPanel) {
        _panelChoice.value = panel
        Log.d("UserInputViewModel", "Panel choice set to $panel")
        roofSize.value?.let {
            updateRoofSize(it)
        }
    }

    fun fetchRoofSizeIfNeeded(lat: Double, lon: Double) {
        val lastCoords = _lastRoofSizeCoords.value
        if (lastCoords == Pair(lat, lon) && _autoFetchedRoofSize.value != null) return

        viewModelScope.launch {
            try {
                val fetched = findRoofSizeAuto(lat, lon)
                if (fetched != null) {
                    _autoFetchedRoofSize.value = fetched
                    _lastRoofSizeCoords.value = Pair(lat, lon)
                } else {
                    _autoFetchedRoofSize.value = null
                    _lastRoofSizeCoords.value = null
                }
            } catch (e: Exception) {
                Log.e("RoofFetch", "Failed to fetch roof size", e)
            }
        }
    }

    fun resetAutoFetchedRoofSize() {
        _autoFetchedRoofSize.value = null
        _lastRoofSizeCoords.value = null
    }
}