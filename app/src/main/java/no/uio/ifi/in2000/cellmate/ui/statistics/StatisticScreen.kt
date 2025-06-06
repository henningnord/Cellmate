package no.uio.ifi.in2000.cellmate.ui.statistics

import no.uio.ifi.in2000.cellmate.ui.userinput.AddressDropdown
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.BottomBar
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.TopBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import no.uio.ifi.in2000.cellmate.ui.mapscreen.MapViewModel
import no.uio.ifi.in2000.cellmate.ui.userinput.UserInputViewModel
import no.uio.ifi.in2000.cellmate.ui.statistics.detailedscreens.UiStateType
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import no.uio.ifi.in2000.cellmate.ui.statistics.components.PanelSelector
import no.uio.ifi.in2000.cellmate.ui.theme.grayText
import java.text.NumberFormat
import java.util.Locale


@Composable
fun StatisticScreen(
    navController: NavController,
    viewModelMap: MapViewModel,
    viewModelStats: StatisticsViewModel,
    viewModelUserInput: UserInputViewModel
) {
    val selectedCoordinates by viewModelMap.coordinates.collectAsState()
    val selectedAddress by viewModelMap.selectedAddress.collectAsState()
    val selectedPostalCode by viewModelMap.postalCode.collectAsState()

    val investment = viewModelStats.investmentResult.collectAsState().value
    val solarEnergy by viewModelStats.expectedEnergyYearly.collectAsState()
    val power by viewModelStats.expectedPowerYearly.collectAsState()
    val saved by viewModelStats.expectedSavedYearly.collectAsState()

    val panelCount by viewModelUserInput.numberOfPanels.collectAsState()
    val maxPanels by viewModelUserInput.maxPanels.collectAsState()

    val dataReady by viewModelStats.dataReady.collectAsState()

    val isLoading = solarEnergy == null || power == null || saved == null
    val showRetryBanner = remember { mutableStateOf(false) }
    val retryTrigger = remember { mutableIntStateOf(0) }

    val permanentFailure = rememberSaveable { mutableStateOf(false) }
    val retryUsed = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(dataReady){if (dataReady) viewModelStats.calculateInvestment(viewModelUserInput.numberOfPanels.value.toInt())}
    LaunchedEffect(key1 = isLoading, key2 = retryTrigger.intValue) {
        if (!isLoading) {
            showRetryBanner.value = false
            return@LaunchedEffect
        }

        delay(40000) // delay when checking to see if we fetched data

        if (selectedAddress == null) return@LaunchedEffect

        if (retryUsed.value) {
            permanentFailure.value = true
            showRetryBanner.value = false
        } else {
            showRetryBanner.value = true
            retryUsed.value = true
        }
    }
    LaunchedEffect(maxPanels) {
        val current = viewModelUserInput.numberOfPanels.value
        if (current > maxPanels) {
            viewModelUserInput.setPanelCount(maxPanels)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBar("Statistikk", MaterialTheme.colorScheme.background) },
        bottomBar = {
            BottomBar(navController)
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = CenterHorizontally

        ) {
            if (showRetryBanner.value) {
                RetryBanner(
                    onRetry = {
                        selectedCoordinates?.let { (lat, lon) ->
                            viewModelStats.refreshAllStatistics(lat, lon, selectedPostalCode ?: "", viewModelUserInput.roofAngle.value ?: 0)
                        }
                        showRetryBanner.value = false
                        retryTrigger.intValue++
                    }
                )
            }
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = CenterHorizontally,
            ) {

                item {
                    val savedHomes by viewModelUserInput.savedHomes.collectAsState()

                    AddressDropdown(
                        selectedAddress = selectedAddress,
                        savedHomes = savedHomes,
                            onSelect = { home ->
                                if (home != null) {
                                    viewModelUserInput.selectSavedHome(home)
                                    viewModelMap.updateSelectedAddress(
                                        home.address,
                                        home.longitude,
                                        home.latitude
                                    )
                                    viewModelStats.updatePanelType(viewModelUserInput.panelChoice.value)
                                    viewModelStats.refreshAllStatistics(
                                        lat = home.latitude,
                                        lon = home.longitude,
                                        postalCode = viewModelMap.postalCode.value ?: "",
                                        roofAngle = viewModelUserInput.roofAngle.value ?: 0
                                    )
                                }
                            },
                        onNewAddressClick = {
                            viewModelMap.setSelectedAddress("")
                            navController.navigate("mapscreen")
                        }
                    )
                }

                item {
                    val panelChoices = viewModelUserInput.panelChoices.values.toList()
                    val selectedPanel = viewModelUserInput.panelChoice.collectAsState().value

                    PanelSelector(
                        selected = selectedPanel,
                        options = panelChoices,
                        onSelect = { panel ->
                            viewModelUserInput.setPanelChoice(panel)
                            viewModelStats.updatePanelType(panel)
                            viewModelStats.calculateInvestment(viewModelUserInput.numberOfPanels.value)
                        }
                    )
                }


            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier.width(360.dp)
                    ) {
                        Column{
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = "Antall solcellepaneler: ${viewModelUserInput.numberOfPanels.collectAsState().value.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }

                            Slider(
                                value = viewModelUserInput.numberOfPanels.collectAsState().value.toFloat(),
                                onValueChange = {
                                    viewModelUserInput.setPanelCount(it)
                                    viewModelStats.onPanelCountChanged(UiStateType.POWER, it)
                                    viewModelStats.onPanelCountChanged(UiStateType.SAVED, it)
                                    viewModelStats.calculateInvestment(it.toInt())
                                },
                                valueRange = 1f..maxPanels,
                                modifier = Modifier
                                    .size(width = 360.dp, height = 44.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.onSurface,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.40f)
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Min", style = MaterialTheme.typography.bodyMedium, color = grayText)
                                Text(text = "Max", style = MaterialTheme.typography.bodyMedium, color = grayText)
                            }
                        }
                    }
                }
            }

                item {
                    StatisticCard(
                        modifier = Modifier,
                        icon = Icons.Outlined.AutoGraph,
                        title = "Lønnsomhet",
                        value = investment?.yearlySaving,
                        unit = "kr spart",
                        metric = "Per år",
                        onClick = { navController.navigate("investmentscreen") },
                        selectedAddress = selectedAddress,
                        permanentFailure = permanentFailure.value

                    )
                }

                item {
                    StatisticCard(
                        modifier = Modifier,
                        icon = Icons.Outlined.AttachMoney,
                        title = "Din produksjonsverdi",
                        value = saved?.times(panelCount),
                        unit = "kr",
                        metric = "Per år",
                        onClick = { navController.navigate("savedscreen") }, // Pass the onClick action here
                        selectedAddress = selectedAddress,
                        permanentFailure = permanentFailure.value
                    )
                }
                item {
                    StatisticCard(
                        modifier = Modifier,
                        icon = Icons.Outlined.Lightbulb,
                        title = "Din strømproduksjon",
                        value = power?.times(panelCount),
                        unit = "kWt",
                        metric = "Per år",
                        onClick = { navController.navigate("powerscreen") },
                        selectedAddress = selectedAddress,
                        permanentFailure = permanentFailure.value


                    )
                }
                item {
                    StatisticCard(
                        modifier = Modifier,
                        icon = Icons.Outlined.WbSunny ,
                        title = "Dine solforhold",
                        value = solarEnergy,
                        unit = "kWt per m²",
                        metric = "Per år",
                        onClick = { navController.navigate("solarinfluxscreen") },
                        selectedAddress = selectedAddress,
                        permanentFailure = permanentFailure.value
                    )
                }
            }
        }
    }
}

fun formatDecimalAuto(value: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("no", "NO")).apply {
        minimumFractionDigits = 0//if (value % 1.0 == 0.0) 0 else 2
        maximumFractionDigits = 0
        isGroupingUsed = true
    }
    return formatter.format(value)
}

@Composable
fun StatisticCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: Double?,
    unit: String,
    metric: String,
    onClick: () -> Unit = {},
    selectedAddress: String?,
    permanentFailure: Boolean

) {
    var showMessage by remember { mutableStateOf(false) }
    val isClickable = selectedAddress != null && value != null && value != 0.0

    val clickModifier = if (isClickable) {
        Modifier.clickable(
            onClickLabel = "Åpner detaljert visning av $title"
        ) { onClick() }
    } else {
        Modifier.clickable(
            onClickLabel = "Kortet er deaktivert – mangler data"
        ) { showMessage = true }
    }

    Card(
        modifier = modifier
            .size(width = 360.dp, height = 100.dp)
            .padding(4.dp)
            .then(clickModifier),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "$title Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(width = 48.dp, height = 48.dp)
                    .align(Alignment.CenterStart)
            )
            Text(
                text = metric,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.TopEnd)
            )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                Spacer(modifier = Modifier.height(8.dp))

                when {
                    permanentFailure -> {
                        Text(
                            text = "Kunne ikke hente data.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }

                    value == null && selectedAddress == null -> {
                        Text(
                            text = "Ingen adresse valgt",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (showMessage) MaterialTheme.colorScheme.error else grayText
                            ),
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }

                    value == null || value == 0.0 -> {
                            RotatingSunIndicator(
                                icon = Icons.Outlined.WbSunny,
                                durationMillis = 3000,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                            )
                    }
                    else -> {
                        Text(
                            text = "${formatDecimalAuto(value)} $unit",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }


@Composable
fun RotatingSunIndicator(
    icon: ImageVector,
    size: Dp = 32.dp,
    durationMillis: Int = 2000,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    Icon(
        imageVector = icon,
        contentDescription = "Laster…",
        modifier = modifier
            .size(size)
            .graphicsLayer { rotationZ = angle },
        tint = MaterialTheme.colorScheme.primary
    )
}


@Composable
fun RetryBanner(onRetry: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFF8E1),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Det tar uvanlig lang tid å hente data.",
                color = Color(0xFF856404),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
            )
            TextButton(onClick = onRetry) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Prøv igjen",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Prøv igjen",
                        color = Color(0xFF4CAF50),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}