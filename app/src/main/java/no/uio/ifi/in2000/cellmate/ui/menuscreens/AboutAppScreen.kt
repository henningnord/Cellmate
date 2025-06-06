package no.uio.ifi.in2000.cellmate.ui.menuscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.BottomBar
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.TopBar

@Composable
fun AboutAppScreen(navController: NavController) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBar("Om appen", MaterialTheme.colorScheme.background)
        },
        bottomBar = {
            BottomBar(navController)
        }
    ) { paddingValues ->
            Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = CenterHorizontally
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(32.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = CenterHorizontally,
            ){
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
                                text = "Velkommen til CellMate!",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        Box(modifier = Modifier.width(360.dp))
                        {
                            IconHeaderRow(Icons.Default.SolarPower, "Hva gjør appen?")
                        }
                        Box(modifier = Modifier.width(360.dp))
                        {
                            Text(
                                text = "CellMate hjelper deg å forstå hvor mye strøm du kan produsere med solcellepaneler på ditt eget tak.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box (modifier = Modifier.width(360.dp)){
                            IconHeaderRow(Icons.Default.Roofing, "Du oppgir:")
                        }
                        Box (modifier = Modifier.width(360.dp)){
                            Text(
                                text = " - Din adresse\n - Ditt årlige strømforbruk \n - Størrelsen på taket ditt",
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
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box (modifier = Modifier.width(360.dp)){
                            IconHeaderRow(Icons.Outlined.AutoGraph, "Din investering")
                        }
                        Box (modifier = Modifier.width(360.dp)){
                            Text(
                                text = "En oversikt over hvor mye solcellepaneler koster, hvor mye du kan få i støtte" +
                                        "i tillegg til hva du vil kunne spare",
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
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box (modifier = Modifier.width(360.dp)){
                            IconHeaderRow(Icons.Outlined.AttachMoney, "Din produksjonsverdi")
                        }
                        Box (modifier = Modifier.width(360.dp)){
                            Text(
                                text = "Beregner hvor mye verdien av produsert strøm er i kroner per år.",
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
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box (modifier = Modifier.width(360.dp)){
                            IconHeaderRow(Icons.Outlined.Lightbulb, "Din strømproduksjon")
                        }
                        Box (modifier = Modifier.width(360.dp)){
                            Text(
                                text = "Vi estimerer hvor mye strøm anlegget ditt kan produsere basert på antall paneler og solforhold.",
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
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box (modifier = Modifier.width(360.dp)){
                            IconHeaderRow(Icons.Outlined.WbSunny, "Sjekk solforhold")
                        }
                        Box (modifier = Modifier.width(360.dp)){
                            Text(
                                text = "Solforhold beregnes med værdata fra samme måned i fjor, slik at beregningene blir så realistiske som mulig.",
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
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box (modifier = Modifier.width(360.dp)){
                            IconHeaderRow(Icons.Default.AutoAwesome, "AI-genererte fun facts")
                        }
                        Box (modifier = Modifier.width(360.dp)){
                            Text(
                                text = "Få morsomme, AI-genererte fakta om hva strømmen din tilsvarer i hverdagslige aktiviteter.",
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
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box (modifier = Modifier.width(360.dp)){
                            IconHeaderRow(Icons.Default.VolunteerActivism, "Vårt mål")
                        }
                        Box (modifier = Modifier.width(360.dp)){
                            Text(
                                text = "Vi ønsker å gjøre solenergi tilgjengelig, forståelig og motiverende for alle – med innsikt, humor og teknologi.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IconHeaderRow(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,

        )
    }
}