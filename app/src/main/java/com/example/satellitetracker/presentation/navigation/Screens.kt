package com.example.satellitetracker.presentation.navigation

sealed class Screens(val route: String) {
    data object ListScreen : Screens("list_screen")
    data object DetailScreen : Screens("detail_screen/{satelliteId}") {
        fun createRoute(satelliteId: Int) = "detail_screen/$satelliteId"
    }
}
