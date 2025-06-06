package no.uio.ifi.in2000.cellmate.ui.statistics

import no.uio.ifi.in2000.cellmate.domain.usecase.CalculateInvestmentUseCase
import no.uio.ifi.in2000.cellmate.domain.usecase.InvestmentInput
import no.uio.ifi.in2000.cellmate.domain.usecase.InvestmentResult
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.cellmate.domain.model.SolarPanel
import no.uio.ifi.in2000.cellmate.data.remote.AreaIdDataSource
import no.uio.ifi.in2000.cellmate.domain.usecase.AreaIdUseCase
import no.uio.ifi.in2000.cellmate.domain.usecase.calculateInfluxPercentage
import no.uio.ifi.in2000.cellmate.domain.usecase.calculateProduction
import no.uio.ifi.in2000.cellmate.domain.usecase.GraphCalculationsUseCase
import no.uio.ifi.in2000.cellmate.domain.usecase.OpenAiUseCase
import no.uio.ifi.in2000.cellmate.domain.usecase.SavingsUseCase
import no.uio.ifi.in2000.cellmate.domain.usecase.WeatherCalculations
import no.uio.ifi.in2000.cellmate.ui.statistics.detailedscreens.UiState
import no.uio.ifi.in2000.cellmate.ui.statistics.detailedscreens.UiStateType
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val weatherCalculations: WeatherCalculations,
    private val savingsUseCase: SavingsUseCase,
    private val graphUseCase: GraphCalculationsUseCase,
    private val openAiUseCase: OpenAiUseCase,
    private val areaIdUseCase: AreaIdUseCase

) : ViewModel() {
    //All expected-variables is per panel
    private val _expectedEnergyYearly = MutableStateFlow<Double?>(null)
    val expectedEnergyYearly = _expectedEnergyYearly.asStateFlow()

    private val _expectedPowerYearly = MutableStateFlow<Double?>(null)
    val expectedPowerYearly = _expectedPowerYearly.asStateFlow()

    private val _expectedSavedYearly = MutableStateFlow<Double?>(null)
    val expectedSavedYearly = _expectedSavedYearly.asStateFlow()

    //Values for every month
    private val _influxList = MutableStateFlow<List<Double>>(emptyList())

    private val _snowcoverlist = MutableStateFlow<List<Double>>(emptyList())

    private val _templist = MutableStateFlow<List<Double>>(emptyList())

    private val _powerlist = MutableStateFlow<List<Double>>(emptyList())
    val powerlist = _powerlist.asStateFlow()

    private val _savingslist = MutableStateFlow<List<Double>>(emptyList())
    val savingslist = _savingslist.asStateFlow()

    private val _panelChoice = MutableStateFlow<SolarPanel?>(SolarPanel(" ", 0.0, 0, 0, 0))
    val panelChoice = _panelChoice.asStateFlow()

    private val _zoneId = MutableStateFlow<String?>(null)

    private var lastUsedCoordinates: Pair<Double, Double>? = null
    private var lastUsedParams: Triple<Pair<Double, Double>, Int, String>? = null
    private var lastUsedPostalCode: String? = null

    private val _funFact = MutableStateFlow<String?>(null)
    val funFact: StateFlow<String?> = _funFact

    private val _prevKwh = MutableStateFlow<Int?>(null)
    val prevKwh: StateFlow<Int?> = _prevKwh

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _roofAngle = MutableStateFlow<Int?>(null)

    private val _dataReady = MutableStateFlow<Boolean>(false)
    val dataReady: StateFlow<Boolean> = _dataReady

    private val _investmentResult = MutableStateFlow<InvestmentResult?>(null)
    val investmentResult: StateFlow<InvestmentResult?> = _investmentResult.asStateFlow()
    private val calculator = CalculateInvestmentUseCase()

    private val _expectedUsage = MutableStateFlow<Int>(0)

    val monthNames = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    var powerYearStats: Map<String, Double> = mapOf("Jan" to 0.0, "Feb" to 0.0, "Mar" to 0.0, "Apr" to 0.0, "May" to 0.0, "Jun" to 0.0, "Jul" to 0.0, "Aug" to 0.0, "Sep" to 0.0, "Oct" to 0.0, "Nov" to 0.0, "Dec" to 0.0)
    var savedYearStats: Map<String, Double> = mapOf("Jan" to 0.0, "Feb" to 0.0, "Mar" to 0.0, "Apr" to 0.0, "May" to 0.0, "Jun" to 0.0, "Jul" to 0.0, "Aug" to 0.0, "Sep" to 0.0, "Oct" to 0.0, "Nov" to 0.0, "Dec" to 0.0)


    fun refreshAllStatistics(lat: Double, lon: Double, postalCode: String, roofAngle: Int) {
        // Prevent redundant refresh
        if (lastUsedCoordinates == Pair(lat, lon) && lastUsedPostalCode == postalCode) return
        // Reset trackers
        lastUsedCoordinates = null
        lastUsedParams = null
        lastUsedPostalCode = postalCode
        _roofAngle.value = roofAngle

        // Reset all state flows to trigger loading UI
        _expectedEnergyYearly.value = null
        _expectedPowerYearly.value = null
        _expectedSavedYearly.value = null
        _influxList.value = emptyList()
        _snowcoverlist.value = emptyList()
        _templist.value = emptyList()
        _influxPercentage.value = null
        _investmentResult.value = null
        _dataReady.value = false
        getAnnualSolarEnergy(lat, lon, postalCode, roofAngle)

    }
    fun refreshAfterPanelChange() {
        viewModelScope.launch {
            Log.d("StatsViewModel", "Refreshing after panel change")

            _expectedPowerYearly.value = null
            _expectedSavedYearly.value = null
            _powerlist.value = emptyList()
            _savingslist.value = emptyList()
            _investmentResult.value = null
            val angle = _roofAngle.value ?: 40

            // Re-generer production and savings data
            getAnnualPower(angle)
        }
    }

    fun getAnnualSolarEnergy(lat: Double, lon: Double, postalCode: String, roofAngle: Int) {
        if (lastUsedCoordinates == Pair(lat, lon)) return
        lastUsedCoordinates = Pair(lat, lon)

        viewModelScope.launch {
            try {
                val energyMonthly = weatherCalculations.getYearlyInflux(lat, lon)
                _influxList.value = energyMonthly
                val energy = energyMonthly.sum()
                _expectedEnergyYearly.value = energy
                Log.d("StatisticsViewModel", "Expected influx yearly: $energy")

                val temp = weatherCalculations.getYearlyTemp(lat, lon)
                val snowCover = weatherCalculations.getYearlySnowCoverage(lat, lon)
                _templist.value = temp
                _snowcoverlist.value = snowCover

                _influxPercentage.value = calculateInfluxPercentage(_influxList.value, _snowcoverlist.value, _templist.value)
                _zoneId.value = areaIdUseCase.fetchPriceAreaId(postalCode)?.priceAreaId ?: "1" //Default to zone 1 if failed
                Log.d("StatisticsViewModel", "Finish influx")
                getAnnualPower(roofAngle)
            } catch (e: Exception) {
                Log.e("StatisticsViewModel", "Error fetching expected energy", e)
            }
        }
    }

    private fun getAnnualPower(roofAngle: Int) {
        viewModelScope.launch {
            try {
                Log.d("StatisticsViewModel", "Calculating production")
                if (_influxList.value.isNotEmpty() &&  _templist.value.isNotEmpty()) {
                    val energy = _influxList.value
                    val temp = _templist.value
                    val snow = _snowcoverlist.value

                    Log.d("StatisticsViewModel", "Calculating production with: Energy=$energy, Temp=$temp, Snow=$snow")

                    if (energy.size == 12 && temp.size == 12 && snow.size == 12) {
                        val powerList = (0 until 12).map { i ->
                            calculateProduction(
                                panelChoice.value,
                                energy[i],
                                temp[i],
                                snow[i],
                                roofAngle
                            )
                        }

                        _powerlist.value = powerList
                        powerYearStats = monthNames.zip(powerlist.value).toMap()

                        val totalPower = powerList.sum()
                        _expectedPowerYearly.value = totalPower
                        Log.d("StatisticsViewModel", "Expected power yearly: total$totalPower")

                        triggerUpdate(UiStateType.POWER)

                        getAnnualSaved(_zoneId.value ?: "1")
                    } else {
                        Log.e("StatisticsViewModel", "Data arrays are incomplete.")
                    }
                } else {
                    Log.e("StatisticsViewModel", "Missing data for power calculation.")
                }
            } catch (e: Exception) {
                Log.e("StatisticsViewModel", "Error calculating expected power yearly", e)
            }
        }
    }


    private fun getAnnualSaved(zone: String) {
        viewModelScope.launch {
            try {
                val savedMonthly = savingsUseCase.calculateMonthlySavings(_powerlist.value, zone)
                _savingslist.value = savedMonthly
                savedYearStats = monthNames.zip(savingslist.value).toMap()

                val saved = savedMonthly.sum()
                _expectedSavedYearly.value = saved
                Log.d("StatisticsViewModel", "Expected saved yearly: $saved")
                triggerUpdate(UiStateType.SAVED)

                _dataReady.value = true

            } catch (e: Exception) {
                Log.e("StatisticsViewModel", "Error fetching expected saved", e)
            }
        }
    }

    private val _powerUiState = MutableStateFlow(
        UiState(
            panelCount = 10f,
        )
    )
    val powerUiState: StateFlow<UiState> = _powerUiState.asStateFlow()

    private val _savedUiState = MutableStateFlow(
        UiState(
            panelCount = 10f,
        )
    )
    val savedUiState: StateFlow<UiState> = _savedUiState.asStateFlow()

    //used in solarscreen
    private val _influxPercentage = MutableStateFlow<Int?>(null)
    val influxPercentage = _influxPercentage.asStateFlow()



    val data: Map<UiStateType, Map<String, Double>>
        get() = mapOf(
            UiStateType.POWER to powerYearStats,
            UiStateType.SAVED to savedYearStats
        )

    fun onPanelCountChanged(type: UiStateType, newCount: Float) {
        val state = getMutableState(type)
        state.update { it.copy(panelCount = newCount) }
        triggerUpdate(type)
    }

    private fun triggerUpdate(type: UiStateType) {
        viewModelScope.launch {
            val state = getMutableState(type)
            val panelCount = state.value.panelCount
            val dataset = data[type] ?: emptyMap()
            updateData(type, state, dataset, panelCount)
        }
    }

    private suspend fun updateData(
        type: UiStateType,
        state: MutableStateFlow<UiState>,
        data: Map<String, Double>,
        panelCount: Float,
    ) {
        val rawYValues = graphUseCase.getAdjustedValues(data, panelCount)

        val yValues = rawYValues.map {
            when {
                it.isFinite() -> it
                else -> {
                    Log.e("updateData", "Ugyldig y-verdi: $it")
                    0f // fallback-value
                }
            }
        }

        if (yValues.any { !it.isFinite() }) {
            Log.e("updateData", "Ugyldige verdier finnes fortsatt i yValues: $yValues")
            return
        }

        val average = graphUseCase.calculateAverage(yValues)
        val averageLine = graphUseCase.generateAverageLine(yValues.size, average)
        val xIndices = graphUseCase.getXIndices(yValues.size)
        val (minY, maxY) = graphUseCase.calculateYAxisRange(yValues.map { it.toDouble() })

        state.update {
            it.copy(
                averageY = average,
                minY = minY,
                maxY = maxY
            )
        }
        val usage = when (type) {
            UiStateType.POWER -> List(12) { _expectedUsage.value / 12  }
            UiStateType.SAVED -> List(12) { 0.0 }
        }
        state.value.chartModelProducer.runTransaction {
            lineSeries {
                series(xIndices, yValues)
                series(xIndices, averageLine)
                series(xIndices, usage )
            }
        }
    }

    private fun getMutableState(type: UiStateType): MutableStateFlow<UiState> {
        return when (type) {
            UiStateType.POWER -> _powerUiState
            UiStateType.SAVED -> _savedUiState
        }
    }

    fun fetchFunFact(kWh: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val fact = openAiUseCase.generateFunFact(kWh)
                _funFact.value = fact
                _prevKwh.value = kWh.toInt()
                _isLoading.value = false
            }
            catch (e: Exception) {
                Log.e("StatisticsViewModel", "Error fetching fun fact", e)
                _isLoading.value = false
            }
        }
    }

    fun setFunFact(funFact: String) {
        _funFact.value = funFact
    }

    fun updatePanelType(panel: SolarPanel?) {
        _panelChoice.value = panel
        Log.d("StatsViewModel", "Panel choice updated: $panel")
        refreshAfterPanelChange()
    }

    fun updateRoofAngle(angle: Int?) {
        _roofAngle.value = angle
        Log.d("StatsViewModel", "RoofAngle updated: $angle")
        refreshAfterPanelChange()
    }
    fun calculateInvestment(panelCount: Int) {
        Log.d("Calc", "Calculating investment")
        val panel = panelChoice.value
        val yearlyValuePerPanel = expectedSavedYearly.value ?: return

        if (panel == null || yearlyValuePerPanel <= 0.0) return

        val input = InvestmentInput(
            panelCount = panelCount,
            pricePerPanel = panel.price,
            effectPerPanel = panel.effect,
            effectGuarantee = panel.effectGuarantee,
            yearlyValuePerPanel = yearlyValuePerPanel
        )

        _investmentResult.value = calculator.calculate(input)
    }
    fun updateExpectedEnergyUsageYearly(value : Int){
        _expectedUsage.value = value
    }
}