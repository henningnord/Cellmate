package no.uio.ifi.in2000.cellmate.ui.menuscreens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.TopBar
import no.uio.ifi.in2000.cellmate.ui.userinput.UserInputViewModel

@Composable
fun AboutPanelsScreen(navController: NavController, userInputViewModel: UserInputViewModel){
    val context = LocalContext.current

    Scaffold  (
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = { TopBar("Om panelene", MaterialTheme.colorScheme.surface, true, navController)
        },

        ){ paddingValues ->

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = CenterHorizontally
        ){
            LazyColumn(
                horizontalAlignment = CenterHorizontally,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(34.dp)
            ) {
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
                                "Her får du informasjon om de ulike panelene du kan velge mellom!",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }

                item {
                    PanelDetails(
                        context, "okonomi", "Økonomi", "• Effekt: 430 W\n" +
                                "• Mål: 1.755 x 1.04 mm (1,83 m²)\n" +
                                "• Garanti: 25 år\n" +
                                "• Pris: 4500 kr"
                    ) {
                        userInputViewModel.setPanelChoice(
                            userInputViewModel.panelChoices.getValue(
                                "okonomi"
                            )
                        )
                        navController.popBackStack()
                    }
                }

                item {
                    PanelDetails(
                        context, "standard", "Standard", "• Effekt: 450 W\n" +
                                "• Mål: 1.755 x 1.04 mm (1,83 m²)\n" +
                                "• Garanti: 25 år\n" +
                                "• Pris: 5850 kr"
                    ) {
                        userInputViewModel.setPanelChoice(
                            userInputViewModel.panelChoices.getValue(
                                "standard"
                            )
                        )
                        navController.popBackStack()
                    }
                }
                item {
                    PanelDetails(
                        context, "premium", "Premium", "• Effekt: 480 W\n" +
                                "• Mål: 1.755 x 1.04 mm (1,83 m²)\n" +
                                "• Garanti: 25 år\n" +
                                "• Pris: 7200 kr"
                    ) {
                        userInputViewModel.setPanelChoice(
                            userInputViewModel.panelChoices.getValue(
                                "premium"
                            )
                        )
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}


@Composable
fun PanelDetails(
    context: Context,
    name : String,
    title : String,
    info: String,
    onClick: () -> Unit
){

    Card(
        modifier = Modifier
            .clickable { onClick() }
            .width(360.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(
                    id = context.resources.getIdentifier(
                        name,
                        "drawable",
                        context.packageName
                    )
                ),
                contentDescription = "$name solcellepanel",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(175.dp)
                    .padding(start = 16.dp, end = 8.dp)
                    .padding(vertical = 16.dp)
                    .aspectRatio(0.5f)
            )
            Column(
                modifier = Modifier
                    .padding(4.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    buildAnnotatedString {
                        val lines = info.split("\n")
                        lines.forEachIndexed { index, line ->
                            val parts = line.split(":")
                            if (parts.size == 2) {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold), ) {
                                    append(parts[0].trim() + ": ")
                                }
                                append(parts[1].trim())
                            } else {
                                append(line)
                            }
                            if (index < lines.lastIndex) append("\n")
                        }
                    }
                )
            }
        }
    }
}
