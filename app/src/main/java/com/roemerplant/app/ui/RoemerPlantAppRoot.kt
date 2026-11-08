package com.roemerplant.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.roemerplant.app.ui.add.AddPlantScreen
import com.roemerplant.app.ui.detail.PlantDetailScreen
import com.roemerplant.app.ui.plants.PlantsScreen
import com.roemerplant.app.ui.settings.SettingsScreen
import com.roemerplant.app.ui.tips.CareTipsScreen
import com.roemerplant.app.ui.theme.RoemerPlantTheme

object Routes {
    const val PLANTS = "plants"
    const val HERBARIUM = "herbarium"
    const val ADD = "add"
    const val SETTINGS = "settings"
    const val TIPS = "tips"
    const val PLANT_DETAIL = "plant/{plantId}"

    fun plantDetail(plantId: Long) = "plant/$plantId"
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val center: Boolean = false
)

private val bottomNavItems = listOf(
    BottomNavItem(Routes.PLANTS, "Garten", Icons.Filled.Eco),
    BottomNavItem(Routes.HERBARIUM, "Herbarium", Icons.Filled.LocalFlorist),
    BottomNavItem(Routes.ADD, "Kamera", Icons.Filled.PhotoCamera, center = true),
    BottomNavItem(Routes.TIPS, "Pflegetipps", Icons.Filled.Spa),
    BottomNavItem(Routes.SETTINGS, "Profil", Icons.Filled.Person)
)

private val topLevelRoutes = setOf(Routes.PLANTS, Routes.HERBARIUM, Routes.ADD, Routes.TIPS, Routes.SETTINGS)

@Composable
fun RoemerPlantAppRoot(mainViewModel: MainViewModel = hiltViewModel()) {
    val theme by mainViewModel.theme.collectAsState()
    RoemerPlantTheme(theme = theme) {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        Scaffold(
            bottomBar = {
                if (currentRoute in topLevelRoutes) {
                    BottomNavBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Routes.PLANTS) { saveState = true }
                                launchSingleTop = true
                            }
                        }
                    )
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
                        onOpenPlant = { id -> navController.navigate(Routes.plantDetail(id)) },
                        onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                        onOpenCollection = { navController.navigate(Routes.HERBARIUM) }
                    )
                }
                composable(Routes.HERBARIUM) {
                    PlantsScreen(
                        onAddPlant = { navController.navigate(Routes.ADD) },
                        onOpenPlant = { id -> navController.navigate(Routes.plantDetail(id)) },
                        onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                        collectionOnly = true
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
                    arguments = listOf(navArgument("plantId") { type = NavType.LongType })
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
                composable(Routes.TIPS) {
                    CareTipsScreen()
                }
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 12.dp,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val active = currentRoute == item.route
                if (item.center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(top = 0.dp)
                            .offset(y = (-10).dp)
                    ) {
                        Surface(
                            onClick = { onNavigate(item.route) },
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = CircleShape,
                            border = BorderStroke(4.dp, MaterialTheme.colorScheme.surfaceContainerLow),
                            shadowElevation = 6.dp,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        Text(
                            item.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                } else if (active) {
                    Surface(
                        onClick = { onNavigate(item.route) },
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                item.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                } else {
                    Surface(
                        onClick = { onNavigate(item.route) },
                        color = androidx.compose.ui.graphics.Color.Transparent,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                item.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
