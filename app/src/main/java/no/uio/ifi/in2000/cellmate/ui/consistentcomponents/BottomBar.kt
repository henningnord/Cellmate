package no.uio.ifi.in2000.cellmate.ui.consistentcomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import no.uio.ifi.in2000.cellmate.ui.theme.grayText


@Composable
fun BottomBar(
    navController: NavController
) {
    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    val items = listOf(
        "userinputscreen" to Pair(Icons.Default.Roofing, "Boliginformasjon"),
        "statisticscreen" to Pair(Icons.Default.SolarPower, "Statistikk"),
    )

    Surface(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 12.dp,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        ) {
            items.forEach { (route, iconAndLabel) ->
                val (icon, label) = iconAndLabel

                NavigationBarItem(
                    selected = currentRoute == route,
                    onClick = {
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Column(horizontalAlignment = CenterHorizontally) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (currentRoute == route) MaterialTheme.colorScheme.primary else grayText
                            )
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = if (currentRoute == route) MaterialTheme.colorScheme.primary else grayText
                            )
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            }

            NavigationBarItem(
                selected = currentRoute == "aboutappscreen",
                onClick = {
                    if (currentRoute != "aboutappscreen") {
                        navController.navigate("aboutappscreen") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Column(horizontalAlignment = CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = if (currentRoute == "aboutappscreen") MaterialTheme.colorScheme.primary else grayText
                        )
                        Text(
                            text = "Om appen",
                            fontSize = 10.sp,
                            color = if (currentRoute == "aboutappscreen") MaterialTheme.colorScheme.primary else grayText
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }

}


@Composable
fun BottomBarUserInput(
    navController: NavController,
    canNavigate: () -> Boolean = { true },
    onProceedNavigation: () -> Unit = {}
) {
    var showInputWarningDialog by remember { mutableStateOf(false) }

    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    val items = listOf(
        "userinputscreen" to Pair(Icons.Default.Roofing, "Boliginformasjon"),
        "statisticscreen" to Pair(Icons.Default.SolarPower, "Statistikk"),
    )

    Surface(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 12.dp,
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {
        NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
            items.forEach { (route, iconAndLabel) ->
                val (icon, label) = iconAndLabel

                NavigationBarItem(
                    selected = currentRoute == route,
                    onClick = {
                        if (currentRoute != route) {
                            if (route == "statisticscreen" && !canNavigate()) {
                                showInputWarningDialog = true
                            } else {
                                onProceedNavigation()
                            }
                        }
                    },
                    icon = {
                        Column(horizontalAlignment = CenterHorizontally) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (currentRoute == route) MaterialTheme.colorScheme.primary else grayText
                            )
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = if (currentRoute == route) MaterialTheme.colorScheme.primary else grayText
                            )
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            }

            NavigationBarItem(
                selected = currentRoute == "aboutappscreen",
                onClick = {
                    if (currentRoute != "aboutappscreen") {
                        navController.navigate("aboutappscreen") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Column(horizontalAlignment = CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = if (currentRoute == "aboutappscreen") MaterialTheme.colorScheme.primary else grayText
                        )
                        Text(
                            text = "Om appen",
                            fontSize = 10.sp,
                            color = if (currentRoute == "aboutappscreen") MaterialTheme.colorScheme.primary else grayText
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }

    if (showInputWarningDialog) {
        AlertDialog(
            onDismissRequest = { showInputWarningDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Advarsel",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = " Manglende informasjon",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Text(
                    "Du har ikke fylt ut takstørrelse eller strømforbruk. Statistikk kan bli unøyaktig. Vil du fortsette?",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showInputWarningDialog = false
                        onProceedNavigation()
                    }
                ) {
                    Text("Fortsett", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showInputWarningDialog = false }
                ) {
                    Text("Fyll ut info", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(16.dp)
        )
    }
}


