package com.saivani.workersbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.saivani.workersbook.data.AppDatabase
import com.saivani.workersbook.repository.WorkersRepository
import com.saivani.workersbook.ui.Screen
import com.saivani.workersbook.ui.screens.*
import com.saivani.workersbook.ui.theme.WorkersBookTheme
import com.saivani.workersbook.viewmodel.WorkersViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = AppDatabase.getInstance(applicationContext).workersDao()
        val repository = WorkersRepository(dao)

        setContent {
            WorkersBookTheme {
                val viewModel: WorkersViewModel = viewModel(factory = WorkersViewModel.factory(repository))
                WorkersBookApp(viewModel)
            }
        }
    }
}

private data class BottomItem(val screen: Screen, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun WorkersBookApp(viewModel: WorkersViewModel) {
    val navController = rememberNavController()

    val items = listOf(
        BottomItem(Screen.Dashboard, stringRes(R.string.nav_dashboard), Icons.Filled.Home),
        BottomItem(Screen.Entries, stringRes(R.string.nav_entries), Icons.Filled.List),
        BottomItem(Screen.Reports, stringRes(R.string.nav_reports), Icons.Filled.BarChart),
        BottomItem(Screen.Permanent, stringRes(R.string.nav_permanent), Icons.Filled.Badge),
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(viewModel, onAddEntry = { navController.navigate(Screen.AddEntry.route) })
            }
            composable(Screen.Entries.route) {
                EntriesScreen(viewModel, onAddEntry = { navController.navigate(Screen.AddEntry.route) })
            }
            composable(Screen.AddEntry.route) {
                AddEntryScreen(viewModel, onSaved = { navController.popBackStack() }, onCancel = { navController.popBackStack() })
            }
            composable(Screen.Reports.route) {
                ReportsScreen(viewModel)
            }
            composable(Screen.Permanent.route) {
                PermanentWorkersScreen(
                    viewModel,
                    onOpenWorker = { id -> navController.navigate(Screen.PermanentDetail.createRoute(id)) }
                )
            }
            composable(Screen.PermanentDetail.route) { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId")?.toLongOrNull() ?: 0L
                PermanentWorkerDetailScreen(viewModel, workerId, onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
fun stringRes(id: Int): String = androidx.compose.ui.res.stringResource(id)
