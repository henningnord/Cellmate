package no.uio.ifi.in2000.cellmate.ui.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.cellmate.ui.theme.grayText
import kotlin.math.roundToInt

@Composable
fun TimeFilterSelector(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
){
    val filters = listOf("Week", "Month", "Year")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        filters.forEach { filter ->
            TextButton(
                onClick = { onFilterSelected(filter) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (filter == selectedFilter) Color(0xFF673AB7) else grayText
                )
            ) {
                Text(
                    text = filter,
                    fontSize = 16.sp,
                    fontWeight = if (filter == selectedFilter) FontWeight.Bold else FontWeight.Normal,
                    color = if (filter == selectedFilter) Color(0xFF673AB7) else grayText
                )
            }
        }
    }
}

@Composable
fun DayDropdown(
    stats: Map<String, Double>
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf<String?>(null) }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFECECEC), RoundedCornerShape(12.dp))
                .clickable { expanded = !expanded }
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Velg dag",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .border(1.dp, grayText, RoundedCornerShape(8.dp))
            ) {
                stats.forEach { (day, value) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = day,
                                fontSize = 16.sp
                            )
                        },
                        onClick = {
                            selectedDay = day
                            expanded = false
                        }
                    )
                }
            }
        }

        // Result card
        selectedDay?.let {
            Spacer(modifier = Modifier.width(12.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
            ) {
                Text(
                    text = "💡 $it – ${stats[it]?.roundToInt()} kwt",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF444444),
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}
