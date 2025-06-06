package no.uio.ifi.in2000.cellmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import no.uio.ifi.in2000.cellmate.ui.consistentcomponents.NetworkViewModel
import no.uio.ifi.in2000.cellmate.ui.mapscreen.MapScreen
import no.uio.ifi.in2000.cellmate.ui.mapscreen.MapViewModel
import no.uio.ifi.in2000.cellmate.ui.menuscreens.AboutAppScreen
import no.uio.ifi.in2000.cellmate.ui.menuscreens.AboutPanelsScreen
import no.uio.ifi.in2000.cellmate.ui.menuscreens.EnovaScreen
import no.uio.ifi.in2000.cellmate.ui.statistics.StatisticScreen
import no.uio.ifi.in2000.cellmate.ui.statistics.StatisticsViewModel
import no.uio.ifi.in2000.cellmate.ui.statistics.detailedscreens.InvestmentScreen
import no.uio.ifi.in2000.cellmate.ui.statistics.detailedscreens.PowerScreen
import no.uio.ifi.in2000.cellmate.ui.statistics.detailedscreens.SavedScreen
import no.uio.ifi.in2000.cellmate.ui.statistics.detailedscreens.SolarScreen
import no.uio.ifi.in2000.cellmate.ui.userinput.UserInputScreen
import no.uio.ifi.in2000.cellmate.ui.userinput.UserInputViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    networkViewModel: NetworkViewModel

) {
    val mapViewModel: MapViewModel = hiltViewModel()
    val statsViewModel: StatisticsViewModel = hiltViewModel()
    val inputViewModel: UserInputViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "statisticscreen",
        modifier = modifier
    ) {
        composable("mapscreen") {
            MapScreen(
                navController = navController,
                viewModel = mapViewModel
            )
        }

        composable("statisticscreen") {
            StatisticScreen(
                navController = navController,
                viewModelMap = mapViewModel,
                viewModelStats = statsViewModel,
                viewModelUserInput = inputViewModel
            )
        }

        composable("powerscreen") {
            PowerScreen(navController, statsViewModel, inputViewModel, networkViewModel)
        }

        composable("savedscreen") {
            SavedScreen(navController, statsViewModel, inputViewModel)
        }

        composable("solarinfluxscreen") {
            SolarScreen(navController, statsViewModel)
        }

        composable("enovascreen") {
            EnovaScreen(navController)
        }

        composable("aboutappscreen") {
            AboutAppScreen(navController)
        }

        composable("userinputscreen") {
            UserInputScreen(
                navController = navController,
                viewModelInput = inputViewModel,
                viewModelMap = mapViewModel,
                viewModelStats = statsViewModel
            )
        }

        composable("investmentscreen") {
            InvestmentScreen(
                navController = navController,
                viewModelUserInput = inputViewModel,
                viewModelStats = statsViewModel
            )
        }

        composable("aboutpanelsscreen") {
            AboutPanelsScreen(
                navController = navController,
                userInputViewModel = inputViewModel
            )
        }
    }
}