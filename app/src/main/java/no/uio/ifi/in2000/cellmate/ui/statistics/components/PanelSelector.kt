package no.uio.ifi.in2000.cellmate.ui.statistics.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import no.uio.ifi.in2000.cellmate.domain.model.SolarPanel


@Composable
fun PanelSelector(
    selected: SolarPanel,
    options: List<SolarPanel>,
    onSelect: (SolarPanel) -> Unit
) {
    val selectedIndex = options.indexOfFirst { it.name == selected.name }

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, panel ->
            val isSelected = index == selectedIndex
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { onSelect(panel) },
                selected = isSelected,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primary,
                    activeContentColor = MaterialTheme.colorScheme.onSurface,
                    inactiveContainerColor = MaterialTheme.colorScheme.surface,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurface
                ),
                label = { Text(panel.name) }
            )
        }
    }
}