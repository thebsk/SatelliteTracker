package com.example.satellitetracker.presentation.navigation

sealed class Screen(val route: String) {
    data object ListScreen : Screen("list_screen")
    data object DetailScreen : Screen("detail_screen/{satelliteId}") {
        fun createRoute(satelliteId: Int) = "detail_screen/$satelliteId"
    }
}
