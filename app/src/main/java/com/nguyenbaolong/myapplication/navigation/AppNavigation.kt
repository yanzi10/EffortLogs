package com.nguyenbaolong.myapplication.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nguyenbaolong.myapplication.core.di.AppContainer
import com.nguyenbaolong.myapplication.feature.habit.presentation.HabitViewModel
import com.nguyenbaolong.myapplication.feature.habit.presentation.HabitViewModelFactory
import com.nguyenbaolong.myapplication.feature.journal.presentation.JournalViewModel
import com.nguyenbaolong.myapplication.feature.journal.presentation.JournalViewModelFactory
import com.nguyenbaolong.myapplication.feature.profile.presentation.ProfileScreen
import com.nguyenbaolong.myapplication.feature.profile.presentation.ProfileViewModel
import com.nguyenbaolong.myapplication.feature.profile.presentation.ProfileViewModelFactory
import com.nguyenbaolong.myapplication.feature.stats.presentation.StatsScreen
import com.nguyenbaolong.myapplication.feature.stats.presentation.StatsViewModel
import com.nguyenbaolong.myapplication.feature.stats.presentation.StatsViewModelFactory
import com.nguyenbaolong.myapplication.feature.task.presentation.TaskViewModel
import com.nguyenbaolong.myapplication.feature.task.presentation.TaskViewModelFactory
import com.nguyenbaolong.myapplication.feature.today.presentation.TodayScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Today : Screen("today", "Today", Icons.Default.Home)
    data object Progress : Screen("progress", "Progress", Icons.Default.Star)
    data object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

val bottomNavItems = listOf(Screen.Today, Screen.Progress, Screen.Profile)

@Composable
fun AppNavigation(
    container: AppContainer,
    startWithVoice: Boolean = false
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(container.taskRepository))
    val habitViewModel: HabitViewModel = viewModel(factory = HabitViewModelFactory(container.habitRepository))
    val journalViewModel: JournalViewModel = viewModel(factory = JournalViewModelFactory(container.journalRepository))
    val statsViewModel: StatsViewModel = viewModel(
        factory = StatsViewModelFactory(container.taskRepository, container.habitRepository, container.journalRepository)
    )
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(container.taskRepository, container.habitRepository, container.journalRepository, container.settingsManager)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                bottomNavItems.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = {
                            Text(
                                screen.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
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
            startDestination = Screen.Today.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Today.route) {
                TodayScreen(
                    taskViewModel = taskViewModel,
                    habitViewModel = habitViewModel,
                    journalViewModel = journalViewModel,
                    initialTab = if (startWithVoice) 2 else 0
                )
            }
            composable(Screen.Progress.route) {
                StatsScreen(viewModel = statsViewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(viewModel = profileViewModel)
            }
        }
    }
}
