package no.uio.ifi.in2000.cellmate.ui.statistics.detailedscreens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Thunderstorm
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.TopBar
import no.uio.ifi.in2000.cellmate.ui.statistics.StatisticsViewModel
import no.uio.ifi.in2000.cellmate.ui.statistics.components.SolarProgressCircle
import no.uio.ifi.in2000.cellmate.ui.statistics.components.InfoBanner


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarScreen(
    navController: NavController,
    viewModelStats: StatisticsViewModel
) {
    val influxPercentage by viewModelStats.influxPercentage.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopBar("Dine solforhold", MaterialTheme.colorScheme.surface, true, navController)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(32.dp) ,
                contentPadding = PaddingValues(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    InfluxPercentageSection(influxPercentage)
                }

                item {
                    RecommendationSection(influxPercentage)
                }
            }
        }
    }
}

@Composable
fun InfluxPercentageSection(influxPercentage: Int?) {

    Text(
        text = "Solforhold",
        style = MaterialTheme.typography.headlineLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = Modifier.padding(bottom = 16.dp),
        textAlign = TextAlign.Center,
    )

    when (influxPercentage) {
        null -> {
            RotatingSunIndicatorSolarScreen(
                icon = Icons.Outlined.WbSunny,
                size = 120.dp,
                durationMillis = 3000
            )
        }

        0 -> {
            Text(
                text = "Kunne ikke hente solforhold. Prøv igjen.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.background
            )
        }

        else -> {
            SolarProgressCircle(influxPercentage)

            Spacer(modifier = Modifier.height(24.dp))

            InfoBanner(
                text = getBannerText(influxPercentage)
            )
        }
    }
}


@Composable
fun getBannerText(percentage: Int): String = when {
    percentage >= 85 -> "Optimalt solforhold for strømproduksjon denne måneden basert på historisk data"
    percentage in 51..84 -> "Brukbare solforhold for strømproduksjon denne måneden basert på historisk data"
    else -> "Lave solforhold for strømproduksjon denne måneden basert på historisk data"
}


@Composable
fun RecommendationSection(influxPercentage: Int?) {
    influxPercentage?.let { influx ->
        val (text, icon) = when {
            influx >= 85 -> "Det er optimal tid for å bruke strøm! Utnytt solforholdene" to Icons.Outlined.WbSunny
            influx in 51..84 -> "Solforholdene er brukbare akkurat nå. Det er fortsatt mulig å få god produksjon." to Icons.Outlined.Cloud
            else -> "Solforholdene er lave akkurat nå. Strømproduksjonen kan være redusert." to Icons.Outlined.Thunderstorm
        }

        Text(
            text = text,
            textAlign = TextAlign.Center,
        )


        Icon (
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 16.dp)
                .size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
@Composable
fun RotatingSunIndicatorSolarScreen(
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






