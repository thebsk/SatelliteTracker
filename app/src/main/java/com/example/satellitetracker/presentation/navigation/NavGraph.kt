package com.example.satellitetracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.satellitetracker.presentation.detail.DetailScreen
import com.example.satellitetracker.presentation.list.ListScreen

@Composable
fun NavGraph(navController: NavHostController, showSnackBar: (String) -> Unit) {
    NavHost(
        navController = navController,
        startDestination = Screen.ListScreen.route
    ) {
        composable(route = Screen.ListScreen.route) {
            ListScreen(
                onSatelliteClick = { satelliteId ->
                    navController.navigate(Screen.DetailScreen.createRoute(satelliteId))
                },
                showSnackBar = showSnackBar
            )
        }
        composable(
            route = Screen.DetailScreen.route,
            arguments = listOf(navArgument("satelliteId") { type = NavType.IntType })
        ) {
            DetailScreen(
                onBackClick = { navController.navigateUp() },
                showSnackBar = showSnackBar
            )
        }
    }
}
