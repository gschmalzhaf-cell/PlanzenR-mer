package com.roemerplant.app.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.roemerplant.app.R
import com.roemerplant.app.ui.add.AddPlantScreen
import com.roemerplant.app.ui.detail.PlantDetailScreen
import com.roemerplant.app.ui.plants.PlantsScreen
import com.roemerplant.app.ui.settings.SettingsScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.roemerplant.app.ui.theme.RoemerPlantTheme

object Routes {
    const val PLANTS = "plants"
    const val ADD = "add"
    const val SETTINGS = "settings"
    const val PLANT_DETAIL = "plant/{plantId}"

    fun plantDetail(plantId: Long) = "plant/$plantId"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoemerPlantAppRoot(mainViewModel: MainViewModel = hiltViewModel()) {
    val theme by mainViewModel.theme.collectAsState()
    RoemerPlantTheme(theme = theme) {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        Scaffold(
            topBar = {
                if (currentRoute != null) {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Eco,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    stringResource(R.string.app_name),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        },
                        actions = {
                            if (currentRoute == Routes.PLANTS) {
                                IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                                    Icon(Icons.Filled.Settings, contentDescription = "Einstellungen")
                                }
                            }
                        }
                    )
                }
            },
            floatingActionButton = {
                if (currentRoute == Routes.PLANTS) {
                    FloatingActionButton(onClick = { navController.navigate(Routes.ADD) }) {
                        Icon(Icons.Filled.Eco, contentDescription = "Pflanze hinzufügen")
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Routes.PLANTS,
                modifier = Modifier.padding(padding)
            ) {
                composable(Routes.PLANTS) {
                    PlantsScreen(
                        onAddPlant = { navController.navigate(Routes.ADD) },
                        onOpenPlant = { id -> navController.navigate(Routes.plantDetail(id)) }
                    )
                }
                composable(Routes.ADD) {
                    AddPlantScreen(
                        onSaved = { id ->
                            navController.popBackStack()
                            navController.navigate(Routes.plantDetail(id))
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = Routes.PLANT_DETAIL,
                    arguments = listOf(navArgument("plantId") { type = androidx.navigation.NavType.LongType })
                ) { entry ->
                    val plantId = entry.arguments?.getLong("plantId") ?: 0L
                    PlantDetailScreen(
                        plantId = plantId,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Routes.SETTINGS) {
                    SettingsScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
