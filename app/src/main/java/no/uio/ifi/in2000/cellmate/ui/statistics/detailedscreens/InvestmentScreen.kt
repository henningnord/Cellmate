package no.uio.ifi.in2000.cellmate.ui.statistics.detailedscreens


import no.uio.ifi.in2000.cellmate.domain.usecase.InvestmentResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.TopBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.cellmate.ui.statistics.StatisticsViewModel
import no.uio.ifi.in2000.cellmate.ui.statistics.components.PanelSelector
import no.uio.ifi.in2000.cellmate.ui.statistics.formatDecimalAuto
import no.uio.ifi.in2000.cellmate.ui.theme.grayText
import no.uio.ifi.in2000.cellmate.ui.userinput.UserInputViewModel
import kotlin.math.roundToInt

@Composable
fun InvestmentScreen(
    navController: NavController,
    viewModelUserInput: UserInputViewModel,
    viewModelStats: StatisticsViewModel
) {
    val selectedPanel = viewModelUserInput.panelChoice.collectAsState().value
    val maxPanels by viewModelUserInput.maxPanels.collectAsState()
    val panelCount by viewModelUserInput.numberOfPanels.collectAsState()
    val valueYearly = viewModelStats.expectedSavedYearly.collectAsState().value ?: 0.0
    val investment = viewModelStats.investmentResult.collectAsState().value ?: InvestmentResult( 0.0,0.0,0.0,0.0,0.0,0.0,0.0 )

    LaunchedEffect(panelCount, selectedPanel, valueYearly) {
        if (valueYearly > 0) {
            viewModelStats.calculateInvestment(panelCount.toInt())
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopBar("Din investering", MaterialTheme.colorScheme.surface, true, navController)
        },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.width(360.dp)
                        ) {
                            Text(
                                "Her får du et estimat på hva solcelleanlegget vil koste, og hvor mye du kan spare!",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }

                item {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                                    onValueChange = {
                                        viewModelUserInput.setPanelCount(it)
                                        viewModelStats.onPanelCountChanged(
                                            UiStateType.POWER,
                                            it
                                        )
                                        viewModelStats.onPanelCountChanged(
                                            UiStateType.SAVED,
                                            it
                                        )
                                    },
                                    valueRange = 1f..maxPanels,
                                    modifier = Modifier
                                        .size(width = 360.dp, height = 44.dp),
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.onSurface,
                                        activeTrackColor = MaterialTheme.colorScheme.primary,
                                        inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                    )
                                )
                            }
                        }
                    }
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
                    InvestmentCard(
                        title = "Totale kostnader",
                        content = {
                            Text("Inkluderer:")
                            Spacer(Modifier.height(8.dp))
                            Text("• $panelCount x ${selectedPanel.name}")
                            Text("• Montering og installasjon")
                            Spacer(Modifier.height(8.dp))
                            Text("Estimert pris:")
                            Text(
                                "${formatDecimalAuto(investment.prePrice)} kr",
                                fontWeight = FontWeight.SemiBold,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    )
                }

                item {
                    InvestmentCard(
                        title = "Enovastøtte",
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                                contentDescription = "trykk her for å lære om enovascreen!",
                                tint = grayText,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        navController.navigate("enovascreen")
                                    }
                            )
                        },
                        content = {
                            Text("Hvordan støtten regnes ut:")
                            Spacer(Modifier.height(8.dp))
                            Text("• kr 7500 ved installasjon")
                            Text("• kr 1250 per kWp installert effekt")
                            Spacer(Modifier.height(8.dp))
                            Text("Estimert støtte:")
                            Text(
                                "${formatDecimalAuto(investment.discount)} kr",
                                fontWeight = FontWeight.SemiBold,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    )
                }
                item {
                    InvestmentCard(
                        title = "Lønnsomhet",
                        content = {
                            Text("Årlig besparelse:")
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "${formatDecimalAuto(investment.yearlySaving)} kr",
                                fontWeight = FontWeight.SemiBold,
                                fontStyle = FontStyle.Italic
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Nedbetalingstid:")
                            Text(
                                "${investment.paybackYears.roundToInt()} år",
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                }
            }
        }
    }

}


@Composable
fun InvestmentCard(
    title: String,
    icon: (@Composable (() -> Unit))? = null,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        modifier = Modifier
            .width(360.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (icon != null ) {
                    icon()
                }
            }
            content()
        }
    }
}
