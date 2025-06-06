package no.uio.ifi.in2000.cellmate.ui.statistics.detailedscreens

import kotlin.collections.mapValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.TopBar
import no.uio.ifi.in2000.cellmate.ui.statistics.StatisticsViewModel
import no.uio.ifi.in2000.cellmate.ui.statistics.components.Linegraph
import no.uio.ifi.in2000.cellmate.ui.userinput.UserInputViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    navController: NavController,
    viewModelStats: StatisticsViewModel,
    viewModelUserInput: UserInputViewModel
) {
    val panelCount by viewModelUserInput.numberOfPanels.collectAsState()
    val maxPanels by viewModelUserInput.maxPanels.collectAsState()

    val state by viewModelStats.savedUiState.collectAsState()
    val currentData = viewModelStats.data[UiStateType.SAVED] ?: emptyMap()
    val adjustedData = remember(currentData, panelCount) {
        currentData.mapValues { it.value * panelCount }
    }
    val kr = state.averageY.roundToInt()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopBar("Din produksjonsverdi", MaterialTheme.colorScheme.surface, true, navController)
        },

    ) { paddingValues ->

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = CenterHorizontally
        ){

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {

                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = CenterHorizontally
                    ){
                        Box(
                            modifier = Modifier.width(360.dp)
                        ) {
                            Text(
                                text = "Her ser du den gjennomsnittlige verdien av strømmen du kan produsere med solcelleanlegget ditt i løpet av et år!",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier.width(360.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                Text(
                                    "Antall solcellepaneler: ${panelCount.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Slider(
                                value = panelCount.toFloat(),
                                onValueChange = { newValue ->
                                    viewModelUserInput.setPanelCount(newValue)
                                    viewModelStats.onPanelCountChanged(UiStateType.SAVED, newValue)
                                    viewModelStats.onPanelCountChanged(UiStateType.POWER, newValue)
                                },
                                valueRange = 1f..maxPanels,
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.onSurface,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier.width(360.dp)
                    ){
                        key(state.averageY) {
                            Linegraph(
                                value = "$kr kr per måned",
                                modelProducer = state.chartModelProducer,
                                dataMap = adjustedData,
                                color = MaterialTheme.colorScheme.primary,
                                yUnit = "kr",
                                modifier = Modifier.fillMaxWidth(),
                                minY = state.minY,
                                maxY = state.maxY
                            )
                        }
                    }
                }

                item{
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.width(360.dp)
                        ) {
                            Text(
                                text = "Forklaring",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier.width(360.dp)
                        ) {
                            Text(
                                text = "Produksjonsverdien er et estimat på hva strømmen du kan produsere ville kostet dersom du måtte kjøpt den.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }

                item{
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.width(360.dp)
                        ) {
                            Text(
                                text = "Beregningen baserer seg på gjennomsnittlige strømpriser de siste tre årene, inkludert merverdiavgift og strømstøtte.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.width(360.dp)
                        ) {
                            Text(
                                text = "Nettleie ikke er inkludert i beregningen.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontStyle = FontStyle.Italic
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}


