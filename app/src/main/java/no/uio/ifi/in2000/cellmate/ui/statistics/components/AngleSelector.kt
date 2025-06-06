package no.uio.ifi.in2000.cellmate.ui.statistics.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AngleSelector(
    selected: String,
    onSelect: (String) -> Unit
) {
    val options = listOf("Ja", "Nei")
    val selectedIndex = options.indexOf(selected)

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(align = Alignment.Start)
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { onSelect(label) },
                selected = isSelected,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primary,
                    activeContentColor = MaterialTheme.colorScheme.onSurface,
                    inactiveContainerColor = MaterialTheme.colorScheme.surface,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurface
                ),
                label = { Text(
                    text =label,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )}
            )
        }
    }
}

