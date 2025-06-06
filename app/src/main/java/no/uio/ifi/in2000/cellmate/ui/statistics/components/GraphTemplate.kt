package no.uio.ifi.in2000.cellmate.ui.statistics.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.dashed
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import no.uio.ifi.in2000.cellmate.ui.theme.grayText
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Linegraph(
    value: String,
    modelProducer: CartesianChartModelProducer,
    dataMap: Map<String, Double>,
    color: Color,
    yUnit: String,
    minY: Double,
    maxY: Double,
    productionScreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(8.dp))

            Graphbuilder(
                modelProducer = modelProducer,
                xLabels = dataMap.keys.toList(),
                lineColor = color,
                yUnit = yUnit,
                minY = minY,
                maxY = maxY,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                if (productionScreen) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Produksjon:", style = MaterialTheme.typography.bodyMedium)
                        Text("─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─", style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Forbruk:", style = MaterialTheme.typography.bodyMedium)
                        Text("────────────────", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Produksjon:", style = MaterialTheme.typography.bodyMedium)
                        Text("─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun Graphbuilder(
    modelProducer: CartesianChartModelProducer,
    xLabels: List<String>,
    lineColor: Color,
    yUnit: String,
    minY: Double,
    maxY: Double,
    modifier: Modifier = Modifier,
) {
    val XAxisValueFormatter = CartesianValueFormatter { _, value, _ ->
        val index = value.roundToInt().coerceIn(0, xLabels.size - 1)
        xLabels[index]
    }

    val YAxisValueFormatter = CartesianValueFormatter { _, value, _ ->
        "${value.toInt()} $yUnit"
    }

    val rangeProvider = CartesianLayerRangeProvider.fixed(minY = minY, maxY = maxY)

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                        areaFill = LineCartesianLayer.AreaFill.single(
                            fill(color = lineColor.copy(alpha = 0.4f))
                        )
                    ),
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(color = grayText)),
                        stroke = LineCartesianLayer.LineStroke.dashed(2.dp, StrokeCap.Butt, 8.dp, 8.dp)
                    ),
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(color = grayText)),
                    )
                ),
                rangeProvider = rangeProvider
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = YAxisValueFormatter,
                label = rememberAxisLabelComponent(
                    color = MaterialTheme.colorScheme.onSurface,
                    textSize = 10.sp
                )
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = XAxisValueFormatter,
                label = rememberAxisLabelComponent(
                    color = MaterialTheme.colorScheme.onSurface,
                    textSize = 10.sp
                ),
                guideline = null
            )
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        scrollState = rememberVicoScrollState(scrollEnabled = false),
    )
}
