package com.nguyenbaolong.myapplication.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nguyenbaolong.myapplication.core.di.AppContainer
import com.nguyenbaolong.myapplication.feature.habit.presentation.HabitScreen
import com.nguyenbaolong.myapplication.feature.habit.presentation.HabitViewModel
import com.nguyenbaolong.myapplication.feature.habit.presentation.HabitViewModelFactory
import com.nguyenbaolong.myapplication.feature.profile.presentation.ProfileScreen
import com.nguyenbaolong.myapplication.feature.stats.presentation.StatsScreen
import com.nguyenbaolong.myapplication.feature.task.presentation.TaskScreen
import com.nguyenbaolong.myapplication.feature.task.presentation.TaskViewModel
import com.nguyenbaolong.myapplication.feature.task.presentation.TaskViewModelFactory

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Today : Screen("today", "Today", Icons.Default.Home)
    object Habits : Screen("habits", "Habits", Icons.Default.Favorite)
    object Progress : Screen("progress", "Progress", Icons.Default.Star)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

val bottomNavItems = listOf(Screen.Today, Screen.Habits, Screen.Progress, Screen.Profile)

@Composable
fun AppNavigation(container: AppContainer) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(container.taskRepository))
    val habitViewModel: HabitViewModel = viewModel(factory = HabitViewModelFactory(container.habitRepository))

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { _ ->
        NavHost(navController = navController, startDestination = Screen.Today.route) {
            composable(Screen.Today.route) { TaskScreen(taskViewModel) }
            composable(Screen.Habits.route) { HabitScreen(habitViewModel) }
            composable(Screen.Progress.route) { StatsScreen() }
            composable(Screen.Profile.route) {
                ProfileScreen(onDeleteAllData = {
                    // TODO Sprint 3: wire full delete
                })
            }
        }
    }
}
