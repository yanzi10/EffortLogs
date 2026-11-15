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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nguyenbaolong.myapplication.core.di.AppContainer
import com.nguyenbaolong.myapplication.feature.habit.presentation.HabitViewModel
import com.nguyenbaolong.myapplication.feature.habit.presentation.HabitViewModelFactory
import com.nguyenbaolong.myapplication.feature.journal.presentation.JournalScreen
import com.nguyenbaolong.myapplication.feature.journal.presentation.JournalViewModel
import com.nguyenbaolong.myapplication.feature.journal.presentation.JournalViewModelFactory
import com.nguyenbaolong.myapplication.feature.onboarding.presentation.OnboardingScreen
import com.nguyenbaolong.myapplication.feature.planner.presentation.PlannerScreen
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
    data object Planner : Screen("planner", "Planner", Icons.Default.DateRange)
    data object Journal : Screen("journal", "Journal", Icons.Default.Edit)
    data object Progress : Screen("progress", "Progress", Icons.Default.Star)
    data object Profile : Screen("profile", "Settings", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    Screen.Today,
    Screen.Planner,
    Screen.Journal,
    Screen.Progress,
    Screen.Profile
)

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

    val appSettings by container.settingsManager.settings.collectAsStateWithLifecycle()
    var forceShowOnboarding by remember { mutableStateOf(false) }

    if (!appSettings.hasSeenOnboarding || forceShowOnboarding) {
        OnboardingScreen(
            onFinish = {
                container.settingsManager.setOnboardingCompleted(true)
                forceShowOnboarding = false
            }
        )
    } else {
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
                startDestination = if (startWithVoice) Screen.Journal.route else Screen.Today.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Today.route) {
                    TodayScreen(
                        taskViewModel = taskViewModel,
                        habitViewModel = habitViewModel,
                        journalViewModel = journalViewModel,
                        settingsManager = container.settingsManager,
                        onNavigateToSchedule = {
                            navController.navigate("planner/0") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToHabits = {
                            navController.navigate("planner/1") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToJournal = {
                            navController.navigate(Screen.Journal.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                composable(
                    route = "planner/{tab}",
                    arguments = listOf(navArgument("tab") { type = NavType.IntType; defaultValue = 0 })
                ) { backStackEntry ->
                    val tab = backStackEntry.arguments?.getInt("tab") ?: 0
                    PlannerScreen(
                        taskViewModel = taskViewModel,
                        habitViewModel = habitViewModel,
                        initialTab = tab
                    )
                }
                composable(Screen.Planner.route) {
                    PlannerScreen(
                        taskViewModel = taskViewModel,
                        habitViewModel = habitViewModel,
                        initialTab = 0
                    )
                }
                composable(Screen.Journal.route) {
                    JournalScreen(viewModel = journalViewModel)
                }
                composable(Screen.Progress.route) {
                    StatsScreen(viewModel = statsViewModel)
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onReplayOnboarding = { forceShowOnboarding = true }
                    )
                }
            }
        }
    }
}
