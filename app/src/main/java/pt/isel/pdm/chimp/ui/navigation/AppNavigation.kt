package pt.isel.pdm.chimp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pt.isel.pdm.chimp.ui.screens.AboutScreen
import pt.isel.pdm.chimp.ui.screens.MainScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(navController)
        }
        composable("about") {
            AboutScreen(navController)
        }
    }
}