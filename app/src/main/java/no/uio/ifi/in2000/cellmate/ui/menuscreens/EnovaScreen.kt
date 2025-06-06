package no.uio.ifi.in2000.cellmate.ui.menuscreens


import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import no.uio.ifi.in2000.cellmate.R
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.TopBar
import com.halilibo.richtext.ui.RichText
import com.halilibo.richtext.markdown.Markdown
import androidx.core.net.toUri

@Composable
fun EnovaScreen(navController: NavController){
    Scaffold  (
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = { TopBar("Enovastøtte", MaterialTheme.colorScheme.surface, true, navController)
        },

    ){ paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
            horizontalAlignment = CenterHorizontally
        ){
            EnovaDetails()
        }
    }
}

@Composable
fun EnovaDetails(){

    val context = LocalContext.current
    val enovaText = remember {
        context.assets.open("textfiles/enovainfo.md")
            .bufferedReader()
            .use{ it.readText()}
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = CenterHorizontally,
        ){
            item {
                Column(
                    horizontalAlignment = CenterHorizontally,
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(R.drawable.enova),
                        contentDescription = "enovapicture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .graphicsLayer { translationY = 0.5f }
                    )
                }
            }

                item {
                    Column(
                        horizontalAlignment = CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                            Text(
                                text = "Informasjon om støtteordning!",
                                style = MaterialTheme.typography.headlineMedium
                            )

                    }
                }

                item {
                    Column (
                        horizontalAlignment = CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    ){
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                item {
                    Column (
                        horizontalAlignment = CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    ){
                        Box (
                            modifier = Modifier.width(360.dp)
                        ){
                            RichText(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Markdown(content = enovaText)
                            }
                        }
                    }
                }
            item {
                Column(
                    horizontalAlignment = CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ){
                    Box(
                        modifier = Modifier.width(360.dp)
                    ){
                        Text(
                            text = "Kilde: Enova - Solcelleanlegg:",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Box(
                        modifier = Modifier.width(360.dp)
                    ){
                        HyperlinkText()
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HyperlinkText() {
    val context = LocalContext.current

    val annotatedText = buildAnnotatedString {
        append("Besøk ")

        pushStringAnnotation(tag = "URL", annotation = "https://www.enova.no/privat/alle-energitiltak/solenergi/solcelleanlegg/")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append("Enova.no")
        }
        pop()

        append(" for mer info.")
    }
    ClickableText(
        text = annotatedText,
        style = MaterialTheme.typography.bodyMedium,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, annotation.item.toUri())
                    context.startActivity(intent)
                }
        }
    )
}


