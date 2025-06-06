package no.uio.ifi.in2000.cellmate.ui.userinput

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.cellmate.ui.mapscreen.MapViewModel
import no.uio.ifi.in2000.cellmate.ui.statistics.StatisticsViewModel
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import no.uio.ifi.in2000.cellmate.data.local.database.entity.SavedHomeEntity
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.BottomBarUserInput
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.TopBar
import no.uio.ifi.in2000.cellmate.ui.statistics.components.AngleSelector
import no.uio.ifi.in2000.cellmate.ui.theme.grayText


@Composable
fun UserInputScreen(
    navController: NavController,
    viewModelInput: UserInputViewModel,
    viewModelMap: MapViewModel,
    viewModelStats: StatisticsViewModel,
) {
    val selectedAddress by viewModelMap.selectedAddress.collectAsState()

    val expectedUsageState by viewModelInput.expectedEnergyUsageYearly.collectAsState()
    var expectedUsage by remember { mutableStateOf(expectedUsageState?.toString() ?: "") }

    val roofSizeState by viewModelInput.roofSize.collectAsState()
    var roofSize by remember { mutableStateOf(roofSizeState?.toString() ?: "") }

    val numberOfPanelsState by viewModelInput.numberOfPanels.collectAsState()

    val coordinates = viewModelMap.coordinates.collectAsState().value
    var showErrors by remember { mutableStateOf(false) }

    val isAddressValid = !selectedAddress.isNullOrBlank()
    val isUsageValid = expectedUsage.isNotBlank()
    val isRoofValid = roofSize.isNotBlank()
    val isInputValid = isAddressValid && isUsageValid && isRoofValid

    val selectedPostalCode by viewModelMap.postalCode.collectAsState()
    var lastRefreshedAddress by remember { mutableStateOf("") }

    val savedHomes by viewModelInput.savedHomes.collectAsState()

    val roofAngleState by viewModelInput.roofAngle.collectAsState()

    var isFlatRoof by remember { mutableStateOf(if (viewModelInput.isRoofFlat.value) "Ja" else "Nei") }

    val autoFetchedRoofSize by viewModelInput.autoFetchedRoofSize.collectAsState()


    LaunchedEffect(coordinates) {
        coordinates?.let { (lon, lat) ->
            Log.i("UserInputScreen", "Coordinates: $lat, $lon")
            viewModelInput.loadCachedData(lat, lon)
            viewModelInput.fetchRoofSizeIfNeeded(lat, lon)

        }
    }

    LaunchedEffect(expectedUsageState) {
        expectedUsage = expectedUsageState?.toString() ?: ""
    }

    LaunchedEffect(roofSizeState) {
        roofSize = roofSizeState?.toString() ?: ""
    }

    LaunchedEffect(expectedUsageState, roofSizeState, numberOfPanelsState, roofAngleState) {
        coordinates?.let { (lon, lat) ->
            val currentAddress = Triple(lat, lon, selectedPostalCode ?: "").toString()
            if (currentAddress != lastRefreshedAddress) {
                viewModelStats.updatePanelType(viewModelInput.panelChoice.value)
                viewModelStats.refreshAllStatistics(
                    lat,
                    lon,
                    selectedPostalCode ?: "",
                    viewModelInput.roofAngle.value ?: 0
                )
                lastRefreshedAddress = currentAddress
            }
        }
    }

    LaunchedEffect(selectedAddress) {
        if (!selectedAddress.isNullOrBlank()) {
            viewModelMap.selectAddress(selectedAddress)
        }
    }

    val saveUserInput = {
        if (isInputValid) {
            viewModelInput.updateExpectedEnergyUsageYearly(expectedUsage.toInt())
            viewModelInput.updateRoofSize(roofSize.toInt())

            coordinates?.let { (lon, lat) ->
                selectedAddress?.let { addr ->
                    viewModelInput.saveCurrentHome(addr, lat, lon)
                }
                viewModelInput.cacheExpectedUsage(lat, lon)
            }
        } else {
            showErrors = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopBar(
                "Boliginformasjon",
                MaterialTheme.colorScheme.background
            )
        },

        bottomBar = {
            BottomBarUserInput(
                navController = navController,
                canNavigate = { isUsageValid && isRoofValid },
                onProceedNavigation = {
                    if (isInputValid) {
                        viewModelInput.updateExpectedEnergyUsageYearly(expectedUsage.toInt())
                        viewModelInput.updateRoofSize(roofSize.toInt())
                        viewModelStats.updatePanelType(viewModelInput.panelChoice.value)
                        viewModelStats.calculateInvestment(viewModelInput.numberOfPanels.value)

                        coordinates?.let { (lon, lat) ->
                            selectedAddress?.let { address ->
                                viewModelInput.saveCurrentHome(address, lat, lon)
                            }
                            viewModelInput.cacheExpectedUsage(lat, lon)
                        }
                    }

                    navController.navigate("statisticscreen") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(32.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        AddressDropdown(
                            selectedAddress = selectedAddress,
                            savedHomes = savedHomes,
                            onSelect = { home ->
                                if (home != null) {
                                    viewModelInput.selectSavedHome(home)
                                    viewModelMap.updateSelectedAddress(
                                        home.address,
                                        home.longitude,
                                        home.latitude
                                    )
                                }
                            },
                            onNewAddressClick = {
                                viewModelMap.setSelectedAddress("")
                                viewModelInput.resetAutoFetchedRoofSize()
                                viewModelMap.setLocation()
                                navController.navigate("mapscreen")
                            }
                        )

                        if (showErrors && !isAddressValid) {
                            Text(
                                text = "Vennligst velg en adresse",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 36.dp, top = 4.dp)
                            )
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.width(360.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(
                                    text = "Forventet årlig strømforbruk",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.width(360.dp)
                        ) {
                            TextField(
                                value = expectedUsage,
                                onValueChange = {
                                    if (it.all { char -> char.isDigit() } && it.length <= 9) {
                                        expectedUsage = it
                                        viewModelInput.updateExpectedEnergyUsageYearly(it.toIntOrNull())

                                    }

                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                maxLines = 1,
                                label = { Text("kWt") },
                                isError = showErrors && !isUsageValid,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                    focusedIndicatorColor = grayText,
                                    unfocusedIndicatorColor = grayText,
                                    cursorColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
                item {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.width(360.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.House,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(
                                    text = "Takstørrelse",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        
                        Box(
                            modifier = Modifier.width(360.dp)
                        ) {
                            TextField(
                                value = roofSize,
                                onValueChange = {
                                    if (it.all { char -> char.isDigit() } && it.length <= 9) {
                                        roofSize = it
                                        viewModelInput.updateRoofSize(it.toIntOrNull())
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                maxLines = 1,
                                label = { Text("m²") },
                                isError = showErrors && !isRoofValid,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                    focusedIndicatorColor = grayText,
                                    unfocusedIndicatorColor = grayText,
                                    cursorColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                    val roofSizeMessage: AnnotatedString? = autoFetchedRoofSize?.let {
                        buildAnnotatedString {
                            append("Vi har automatisk hentet takstørrelsen din: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("$it m²")
                            }
                        }
                    } ?: buildAnnotatedString {
                        append("Vi fant ikke din takstørrelse automatisk")
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.width(360.dp)
                        ) {
                            roofSizeMessage?.let { message ->
                                Text(
                                    text = message,
                                    fontSize = 14.sp,
                                    color = grayText
                                )
                            }
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = CenterHorizontally

                    ) {
                        Box(
                            modifier = Modifier.width(360.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChangeHistory,
                                    contentDescription = "Vinkel",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(
                                    text = "Er taket ditt flatt?",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier.width(360.dp)
                        ) {
                            AngleSelector(
                                selected = isFlatRoof,
                                onSelect = { selected ->
                                    isFlatRoof = selected
                                    if (selected == "Ja") {
                                        viewModelInput.updateIsRoofFlat(true)
                                        viewModelInput.updateRoofAngle(0)
                                    } else {
                                        viewModelInput.updateIsRoofFlat(false)
                                        viewModelInput.updateRoofAngle(40)
                                    }
                                }
                            )
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        Row {
                            Text(
                                text = "Velg paneltype:",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                                contentDescription = "Om paneler",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        saveUserInput()
                                        navController.navigate("aboutpanelsscreen")
                                    }
                            )
                        }
                    }
                }

                item{
                    val selectedPanel = viewModelInput.panelChoice.collectAsState().value
                    val panelChoices = viewModelInput.panelChoices
                    val context = LocalContext.current

                    Column (
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = CenterHorizontally
                    ){
                        LazyRow {
                            panelChoices.forEach { (key, panel) ->
                                item {
                                    val isSelected = panel == selectedPanel

                                    Card(
                                        modifier = Modifier
                                            .clickable {
                                                viewModelInput.setPanelChoice(
                                                    panel
                                                )
                                            }
                                            .padding(horizontal = 8.dp)
                                            .width(100.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 4.dp
                                        ),
                                        border = if (isSelected) BorderStroke(
                                            2.dp,
                                            MaterialTheme.colorScheme.primary
                                        ) else null
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(12.dp)
                                        ) {
                                            Text(
                                                text = panel.name,
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                style = MaterialTheme.typography.bodyLarge,
                                                textAlign = TextAlign.Center
                                            )
                                            Image(
                                                painter = painterResource(
                                                    id = context.resources.getIdentifier(
                                                        key,
                                                        "drawable",
                                                        context.packageName
                                                    )
                                                ),
                                                contentDescription = "paneltype",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(4.dp)
                                                    .aspectRatio(0.5f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                ElevatedButton(
                    onClick = {
                        if (isInputValid) {
                            viewModelInput.updateExpectedEnergyUsageYearly(expectedUsage.toInt())
                            viewModelStats.updateExpectedEnergyUsageYearly(expectedUsage.toInt())
                            viewModelInput.updateRoofSize(roofSize.toInt())
                            viewModelStats.updatePanelType(viewModelInput.panelChoice.value)
                            viewModelStats.updateRoofAngle(viewModelInput.roofAngle.value)
                            viewModelStats.calculateInvestment(viewModelInput.numberOfPanels.value)


                            coordinates?.let { (lon, lat) ->
                                selectedAddress?.let { address ->
                                    viewModelInput.saveCurrentHome(
                                        address,
                                        lat,
                                        lon
                                    )
                                }

                                viewModelInput.cacheExpectedUsage(lat, lon)
                            }

                            navController.navigate("statisticscreen")
                        } else {
                            showErrors = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .wrapContentWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(100.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(3.dp)
                ) {
                    Text(
                        text = "Lagre informasjon",
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}


@Composable
fun AddressDropdown(
    selectedAddress: String?,
    savedHomes: List<SavedHomeEntity>,
    onSelect: (SavedHomeEntity?) -> Unit,
    onNewAddressClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val recentAddresses = savedHomes
        .map { it.address }
        .filter { it != selectedAddress }
        .take(3)
    val options = recentAddresses + listOf("Velg ny adresse")
    val displayText = selectedAddress ?: "Ingen adresse valgt"

    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(100.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .width(360.dp)
                .height(56.dp)
                .clickable { expanded = !expanded }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Adresse",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Lukk meny" else "Åpne meny",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    if (expanded) {
        Spacer(Modifier.height(4.dp))
        Surface(
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .width(360.dp)
                .wrapContentHeight()
        ) {
            Column {
                options.forEachIndexed { index, option ->
                    Text(
                        text = option,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expanded = false
                                if (option == "Velg ny adresse") {
                                    onSelect(null)
                                    onNewAddressClick()
                                } else {
                                    val home = savedHomes.find { it.address == option }
                                    onSelect(home)
                                }
                            }
                            .padding(16.dp)
                    )

                    if (index < options.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 1.dp,
                            color = grayText)
                    }
                }
            }
        }
    }
}
