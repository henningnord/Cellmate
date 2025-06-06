package no.uio.ifi.in2000.cellmate.ui.statistics.detailedscreens


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.cellmate.R
import no.uio.ifi.in2000.cellmate.domain.usecase.NetworkStatus
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.NetworkViewModel
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.TopBar
import no.uio.ifi.in2000.cellmate.ui.statistics.RotatingSunIndicator
import no.uio.ifi.in2000.cellmate.ui.statistics.StatisticsViewModel
import no.uio.ifi.in2000.cellmate.ui.statistics.components.Linegraph
import no.uio.ifi.in2000.cellmate.ui.userinput.UserInputViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PowerScreen(
    navController: NavController,
    viewModelStats: StatisticsViewModel,
    viewModelUserInput: UserInputViewModel,
    networkViewModel: NetworkViewModel
) {
    val panelCount by viewModelUserInput.numberOfPanels.collectAsState()
    val maxPanels by viewModelUserInput.maxPanels.collectAsState()
    val state by viewModelStats.powerUiState.collectAsState()
    val currentData = viewModelStats.data[UiStateType.POWER] ?: emptyMap()
    val adjustedData = remember(currentData, panelCount) {
        currentData.mapValues { it.value * panelCount }
    }
    val funFact by viewModelStats.funFact.collectAsState()
    val kWh = state.averageY.roundToInt()
    val prevKwh = viewModelStats.prevKwh.collectAsState()
    val isLoading by viewModelStats.isLoading.collectAsState()
    val customIcon: Painter = painterResource(id = R.drawable.generative)

    val networkStatus by networkViewModel.networkStatus.collectAsState()
    val isNetworkAvailable = networkStatus == NetworkStatus.Available

    if (funFact != null && funFact!!.isNotBlank() && kWh != prevKwh.value) {
        viewModelStats.setFunFact("")
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopBar("Din strømproduksjon", MaterialTheme.colorScheme.surface, true, navController)
        },

    ) { paddingValues ->

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = CenterHorizontally
        ) {
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
                                text = "Her ser du hvor mye strøm solcelleanlegget ditt i gjennomsnitt kan produsere i løpet av ett år!",
                                style = MaterialTheme.typography.bodyMedium,
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
                                    viewModelStats.onPanelCountChanged(UiStateType.POWER, newValue)
                                    viewModelStats.onPanelCountChanged(UiStateType.SAVED, newValue)
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
                                value = "$kWh kWt per måned",
                                modelProducer = state.chartModelProducer,
                                dataMap = adjustedData,
                                color = MaterialTheme.colorScheme.primary,
                                yUnit = "kWt",
                                modifier = Modifier.fillMaxWidth(),
                                minY = state.minY,
                                maxY = state.maxY,
                                productionScreen = true
                            )
                        }
                    }
                }

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
                                text = "Produksjonsestimatet baserer seg på paneltype, solinnstråling og værforhold, justert etter hvor mange paneler som inngår i anlegget.",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }

                item {
                    ElevatedButton(
                        onClick = {
                            if (isNetworkAvailable) {
                                viewModelStats.fetchFunFact(kWh.toString())
                            }
                        },
                        enabled = !isLoading && isNetworkAvailable,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = MaterialTheme.colorScheme.background,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 12.dp,
                            disabledElevation = 4.dp
                        ),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier
                            .padding(horizontal = 30.dp)
                            .height(45.dp)
                    ) {
                        if (isLoading) {
                            RotatingSunIndicator(
                                icon = Icons.Outlined.WbSunny,
                                size = 20.dp,
                                durationMillis = 3000
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Laster...")
                        } else {
                            Icon(
                                painter = customIcon,
                                contentDescription = "AI",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(20.dp)
                            )
                            Text("Generer en funfact")
                        }
                    }
                    if (!isNetworkAvailable) {
                        viewModelStats.setFunFact("")
                        Text(
                            text = "Nettverktilkobling kreves for å generere funfacts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.background,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }

                funFact?.takeIf { it.isNotBlank() }?.let {
                    item {
                        Box(
                            modifier = Modifier.width(360.dp)
                        ){
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.5.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
