package com.saivani.workersbook.ui

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Entries : Screen("entries")
    data object AddEntry : Screen("add_entry")
    data object Reports : Screen("reports")
    data object Permanent : Screen("permanent")
    data object PermanentDetail : Screen("permanent_detail/{workerId}") {
        fun createRoute(workerId: Long) = "permanent_detail/$workerId"
    }
}
