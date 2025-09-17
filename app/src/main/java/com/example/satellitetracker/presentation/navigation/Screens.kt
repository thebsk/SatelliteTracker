package com.example.satellitetracker.presentation.navigation

object NavArgs {
    const val SATELLITE_ID: String = "satelliteId"
}

sealed class Screens(val route: String) {
    data object ListScreen : Screens("list_screen")
    data object DetailScreen : Screens("detail_screen/{${NavArgs.SATELLITE_ID}}") {
        fun createRoute(satelliteId: Int) = "detail_screen/$satelliteId"
    }
}
