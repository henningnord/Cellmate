package no.uio.ifi.in2000.cellmate.ui.statistics.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun SolarProgressCircle(percentage: Int) {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = percentage) {
        progress = 0f
        animate(
            initialValue = 0f,
            targetValue = percentage / 100f,
            animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
        ) { value, _ ->
            progress = value
        }
    }

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(200.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 10.dp,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.40f),
            strokeCap = StrokeCap.Round
        )
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun PercentageBanner(percentage: Int) {
    val (percentageColor, backgroundColor) = when {
        percentage >= 85 -> Color(0xFF4CAF50) to Color(0xFFC8E6C9)
        percentage >= 50 -> Color(0xFFFFC107) to Color(0xFFFFF9C4)
        else -> Color(0xFFF44336) to Color(0xFFFFCDD2)
    }

    Text(
        text = "$percentage%",
        color = percentageColor,
        textAlign = TextAlign.Center,
        fontStyle = FontStyle.Normal,
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
fun InfoBanner(text: String) {

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        modifier = Modifier
            .width(360.dp)
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(24.dp)
            )
    }
}
