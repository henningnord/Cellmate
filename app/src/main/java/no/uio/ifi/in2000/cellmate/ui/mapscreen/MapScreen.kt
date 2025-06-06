package no.uio.ifi.in2000.cellmate.ui.mapscreen

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import androidx.navigation.NavController
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.search.ApiType
import com.mapbox.search.ResponseInfo
import com.mapbox.search.ReverseGeoOptions
import com.mapbox.search.SearchCallback
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.result.SearchResult
import no.uio.ifi.in2000.cellmate.R
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import com.mapbox.common.Cancelable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, viewModel: MapViewModel) {
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    var searchText by remember { mutableStateOf(selectedAddress ?: "") }
    val searchResults by viewModel.searchResults.collectAsState(initial = emptyList())
    var showSuggestions by remember { mutableStateOf(false) }
    val selectedPlace by viewModel.selectedPlace.collectAsState()
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var annotationManager by remember { mutableStateOf<PointAnnotationManager?>(null) }
    val context = LocalContext.current
    val pinLocation by viewModel.pinLocation.collectAsState()

    var currentStyleUri by remember { mutableStateOf("mapbox://styles/martiwj/cm8sntg5y007p01s580222563") }
    val satelliteStyleUri = "mapbox://styles/mapbox/satellite-v9"
    val regularStyleUri = "mapbox://styles/martiwj/cm8sntg5y007p01s580222563"
    val zoomThreshold = 15.0
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val newLocation = viewModel.newLocation.collectAsState().value

    // Function to perform reverse geocoding to avoid much boilerplate
    fun performReverseGeocoding(point: Point) {
        val searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
            ApiType.SEARCH_BOX,
            SearchEngineSettings()
        )
        val reverseGeoOptions = ReverseGeoOptions(center = point)
        searchEngine.search(reverseGeoOptions, object : SearchCallback {
            override fun onResults(results: List<SearchResult>, responseInfo: ResponseInfo) {
                if (results.isNotEmpty()) {
                    val result = results[0]
                    Log.d("ReverseGeocoding", "Result: ${result.name}, Full address: ${result.fullAddress}")

                    // Use the address directly from the search result
                    val addressText = result.fullAddress ?: result.address?.formattedAddress() ?: result.name
                    val postalCode = result.address?.postcode

                    // Use the new method to set address with coordinates
                    viewModel.setAddressWithCoordinates(addressText, point.longitude(), point.latitude())
                    viewModel.setPostalCode(postalCode)
                    searchText = addressText
                }
            }

            override fun onError(e: Exception) {
                Log.e("ReverseGeocoding", "Error: ${e.message}")
            }
        })
    }

    // Permission launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationPermissionsGranted = permissions.entries.all { it.value }
        if (locationPermissionsGranted) {
            // Refresh location permissions in the map
            mapView?.location?.updateSettings {
                enabled = true
            }
        }
    }
    // Check and request permissions when the screen loads
    LaunchedEffect(Unit) {
        if (!PermissionsManager.areLocationPermissionsGranted(context)) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    LaunchedEffect(viewModel.selectedAddress.collectAsState().value) {
        val address = viewModel.selectedAddress.value
        if (address != null && address != searchText) {
            searchText = address
        }
    }

    Scaffold{ paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            var cameraChangedCancelable: Cancelable? = null

            AndroidView(
                factory = { context ->
                    MapView(context).also {
                        it.mapboxMap.loadStyle(currentStyleUri)
                        mapView = it
                        annotationManager = it.annotations.createPointAnnotationManager()

                        cameraChangedCancelable = it.mapboxMap.subscribeCameraChanged { cameraState ->
                            val zoomLevel = it.mapboxMap.cameraState.zoom

                            if (zoomLevel >= zoomThreshold && currentStyleUri != satelliteStyleUri) {
                                currentStyleUri = satelliteStyleUri
                                it.mapboxMap.loadStyle(currentStyleUri) { _ ->
                                    pinLocation?.let { point ->
                                        annotationManager?.deleteAll()
                                        val bitmap = bitmapFromDrawableRes(context, R.drawable.red_marker)
                                        val pointAnnotationOptions = PointAnnotationOptions()
                                            .withPoint(point)
                                            .withIconImage(bitmap)
                                        annotationManager?.create(pointAnnotationOptions)
                                    }
                                }
                            } else if (zoomLevel < zoomThreshold && currentStyleUri != regularStyleUri) {
                                currentStyleUri = regularStyleUri
                                it.mapboxMap.loadStyle(currentStyleUri) { _ ->
                                    pinLocation?.let { point ->
                                        annotationManager?.deleteAll()
                                        val bitmap = bitmapFromDrawableRes(context, R.drawable.red_marker)
                                        val pointAnnotationOptions = PointAnnotationOptions()
                                            .withPoint(point)
                                            .withIconImage(bitmap)
                                        annotationManager?.create(pointAnnotationOptions)
                                    }
                                }
                            }
                        }

                        it.location.updateSettings {
                            locationPuck = createDefault2DPuck(withBearing = true)
                            enabled = true
                            puckBearing = PuckBearing.COURSE
                            puckBearingEnabled = true
                        }
                        it.mapboxMap.setCamera(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(10.7522, 59.9139))
                                .zoom(10.0)
                                .build()
                        )

                        it.mapboxMap.addOnMapClickListener { point ->
                            annotationManager?.let { manager ->
                                manager.deleteAll()
                                val bitmap = bitmapFromDrawableRes(context, R.drawable.red_marker)
                                val pointAnnotationOptions = PointAnnotationOptions()
                                    .withPoint(point)
                                    .withIconImage(bitmap)
                                manager.create(pointAnnotationOptions)
                                viewModel.setPinLocation(point)
                                performReverseGeocoding(point)
                            }
                            true
                        }
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 32.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                Box {
                    TextField(
                        value = searchText,
                        onValueChange = { query ->
                            searchText = query
                            showSuggestions = query.isNotEmpty()
                            viewModel.searchLocations(query)
                        },
                        placeholder = { Text("Adresse") },
                        leadingIcon = {
                            if (showSuggestions) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Tilbake",
                                    modifier = Modifier.clickable {
                                        showSuggestions = false
                                        val coords = viewModel.coordinates.value
                                        if (coords != null) {
                                            viewModel.setAddressWithCoordinates(
                                                searchText,
                                                coords.first,
                                                coords.second
                                            )
                                        } else {
                                            viewModel.selectAddress(searchText)
                                        }
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                    }
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Min posisjon"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                viewModel.selectAddress(searchText)
                                showSuggestions = false
                            }
                        ),
                        modifier = Modifier
                            .shadow(8.dp, shape = RoundedCornerShape(100.dp))
                            .size(width = 360.dp, height = 56.dp)
                            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(100.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(100.dp),
                    )

                    if (showSuggestions && searchResults.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .width(360.dp)
                                .wrapContentHeight()
                                .offset(y = 60.dp)
                                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                                .shadow(6.dp, shape = RoundedCornerShape(12.dp))
                        ) {
                            searchResults.take(10).forEach { suggestion ->
                                ListItem(
                                    headlineContent = { Text(suggestion.name) },
                                    supportingContent = { Text(suggestion.formattedAddress ?: "") },
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.selectSuggestion(suggestion)
                                            searchText = suggestion.formattedAddress ?: suggestion.name
                                            showSuggestions = false
                                            focusManager.clearFocus()
                                            keyboardController?.hide()
                                        },
                                    colors = ListItemDefaults.colors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                ElevatedButton(
                    onClick = {
                        if (searchText.isNotEmpty()) {
                            showSuggestions = false
                        }
                        navController.navigate("userinputscreen")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(55.dp)
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(100.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(3.dp)
                ) {
                    Text(
                        text = "Gå videre",
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp
                    )
                }
            }
            // Zoom controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
            ) {
                FAB(
                    onClick = {
                        val locationService = LocationServiceFactory.getOrCreate()
                        val result = locationService.getDeviceLocationProvider(null)
                        if (result.isValue) {
                            val locationProvider = result.value!!
                            locationProvider.getLastLocation { location ->
                                location?.let {
                                    val point = Point.fromLngLat(it.longitude, it.latitude)

                                    mapView?.mapboxMap?.easeTo(
                                        CameraOptions.Builder()
                                            .center(point)
                                            .zoom(18.0)
                                            .pitch(0.0)
                                            .build(),
                                        MapAnimationOptions.Builder().duration(1500L).build()
                                    )

                                    annotationManager?.let { manager ->
                                        manager.deleteAll()
                                        val bitmap = bitmapFromDrawableRes(context, R.drawable.red_marker)
                                        val pointAnnotationOptions = PointAnnotationOptions()
                                            .withPoint(point)
                                            .withIconImage(bitmap)
                                        manager.create(pointAnnotationOptions)
                                    }

                                    viewModel.setPinLocation(point)
                                    performReverseGeocoding(point)
                                }
                            }
                        }
                    },
                    icon = Icons.Default.MyLocation,
                    contentDescription = "My Location",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FAB(
                    onClick = {
                        mapView?.mapboxMap?.cameraState?.zoom?.let { currentZoom ->
                            mapView?.mapboxMap?.easeTo(
                                CameraOptions.Builder()
                                    .zoom(currentZoom + 1.0)
                                    .build()
                            )
                        }
                    },
                    icon = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Zoom In",
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FAB(
                    onClick = {
                        mapView?.mapboxMap?.cameraState?.zoom?.let { currentZoom ->
                            mapView?.mapboxMap?.easeTo(
                                CameraOptions.Builder()
                                    .zoom(currentZoom - 1.0)
                                    .build()
                            )
                        }
                    },
                    icon = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Zoom Out",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ){
                FAB(
                    onClick = {
                        navController.navigate("userinputscreen")
                    },
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }

    LaunchedEffect(newLocation) {
        if (newLocation) {
            mapView?.mapboxMap?.let { mapboxMap ->
                mapboxMap.easeTo(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(10.7522, 59.9139))
                        .zoom(10.0)
                        .build(),
                    MapAnimationOptions.Builder().duration(1500L).build()
                )
                annotationManager?.deleteAll()
                searchText = ""
                viewModel.setLocation()
            }
        }
    }

    LaunchedEffect(selectedPlace) {
        selectedPlace?.coordinate?.let { coord ->
            mapView?.mapboxMap?.let { mapboxMap ->
                val mapAnimationOptions = MapAnimationOptions.Builder().duration(1500L).build()
                val point = Point.fromLngLat(coord.longitude(), coord.latitude())
                mapboxMap.easeTo(
                    CameraOptions.Builder()
                        .center(point)
                        .zoom(18.0)
                        .build(),
                    mapAnimationOptions
                )
                annotationManager?.let { manager ->
                    manager.deleteAll()
                    val bitmap = bitmapFromDrawableRes(context, R.drawable.red_marker)
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(point)
                        .withIconImage(bitmap)
                    manager.create(pointAnnotationOptions)
                }
            }
        }
    }
    // Makes sure the manually selected pin stays in the map when navigating between screens
    LaunchedEffect(mapView, annotationManager, pinLocation) {
        if (mapView != null && annotationManager != null && pinLocation != null) {
            annotationManager?.deleteAll()
            val bitmap = bitmapFromDrawableRes(context, R.drawable.red_marker)
            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(pinLocation!!)
                .withIconImage(bitmap)
            annotationManager?.create(pointAnnotationOptions)
        }
    }
}

// Helper function for formatting the pin icon shown on the map
private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int): Bitmap {
    val drawable = AppCompatResources.getDrawable(context, resourceId)
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }

    val bitmap = createBitmap(drawable?.intrinsicWidth ?: 100, drawable?.intrinsicHeight ?: 100)
    val canvas = Canvas(bitmap)
    drawable?.setBounds(0, 0, canvas.width, canvas.height)
    drawable?.draw(canvas)
    return bitmap
}

@Composable
fun FAB(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier : Modifier
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 3.dp,
        modifier = modifier
            .size(35.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(6.dp)
        )
    }
}