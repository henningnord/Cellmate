package no.uio.ifi.in2000.cellmate.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import no.uio.ifi.in2000.cellmate.ui.navigation.AppNavHost
import no.uio.ifi.in2000.cellmate.domain.usecase.NetworkStatus
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.NetworkErrorDialog
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.NetworkStatusBanner
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.NetworkViewModel
import no.uio.ifi.in2000.cellmate.ui.frontpage.SplashOverlay
import no.uio.ifi.in2000.cellmate.ui.theme.CellMateTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var keepSplashScreen = true
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        super.onCreate(savedInstanceState)

        setContent {
            var showSplash by rememberSaveable { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                delay(900)
                keepSplashScreen = false
                delay(2500)
                showSplash = false
            }

            CellMateTheme() {
                Surface {
                    if (showSplash) {
                        SplashOverlay()
                    } else {
                        val navController = rememberNavController()
                        val viewModel: NetworkViewModel = hiltViewModel()
                        val networkStatus by viewModel.networkStatus.collectAsState()

                        val showDialog = remember { mutableStateOf(false) }
                        val hasDismissedDialog = remember { mutableStateOf(false) }
                        val lastStatus = remember { mutableStateOf(NetworkStatus.Available) }

                        // Show popup once per network loss
                        if (networkStatus == NetworkStatus.Lost &&
                            lastStatus.value != NetworkStatus.Lost &&
                            !hasDismissedDialog.value
                        ) {
                            showDialog.value = true
                        }

                        // Track last status to prevent duplicate dialog
                        if (lastStatus.value != networkStatus) {
                            lastStatus.value = networkStatus
                            if (networkStatus == NetworkStatus.Available) {
                                hasDismissedDialog.value = false
                            }
                        }

                        Column {
                            if (networkStatus == NetworkStatus.Lost) {
                                NetworkStatusBanner()
                            }

                            AppNavHost(navController = navController,
                                networkViewModel = viewModel
                            )
                        }

                        if (showDialog.value) {
                            NetworkErrorDialog(onDismiss = {
                                showDialog.value = false
                                hasDismissedDialog.value = true
                            })
                        }
                    }
                }
            }
        }
    }
}