package no.uio.ifi.in2000.cellmate.ui.frontpage

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment.Companion.CenterHorizontally


@Composable
fun SplashOverlay() {
    var atEnd by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1350)
        atEnd = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (atEnd) 1f else 0f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 1500)
    )

    val lineWidth by animateDpAsState(
        targetValue = if (atEnd) 145.dp else 0.dp,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 1800)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "CellMate",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF484C52),
                modifier = Modifier.alpha(alpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(lineWidth)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}



