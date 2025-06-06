package no.uio.ifi.in2000.cellmate.ui.frontpage

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.cellmate.R

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var isSplashFinished by remember { mutableStateOf(false) }


    LaunchedEffect(true) {
        Handler(Looper.getMainLooper()).postDelayed({
            isSplashFinished = true
            onTimeout()
        }, 3000)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally
        ) {
            SunGrid()
        }
    }
}

@Composable
fun SunGrid() {
    val sunPattern = listOf(
        listOf(0),
        listOf(0, 1),
        listOf(0, 1, 2),
        listOf(0, 1, 2, 3),
        listOf(0, 1, 2),
        listOf(0, 1),
        listOf(0)
    )

    Column(
        modifier = Modifier.padding(start = 0.dp)
    ) {
        sunPattern.forEachIndexed { rowIndex, cols ->
            Row {
                for (i in 0..3) {
                    val isVisible = cols.contains(i)
                    val sunRes = if (rowIndex % 2 == 0) R.drawable.sun2 else R.drawable.sun3
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(8.dp)
                    ) {
                        if (isVisible) {
                            Image(
                                painter = painterResource(id = sunRes),
                                contentDescription = "Sun",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(onTimeout = {})
}
