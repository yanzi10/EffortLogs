package com.nguyenbaolong.myapplication.feature.planner.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyenbaolong.myapplication.core.ui.AppIcons
import com.nguyenbaolong.myapplication.feature.habit.presentation.HabitScreen
import com.nguyenbaolong.myapplication.feature.habit.presentation.HabitViewModel
import com.nguyenbaolong.myapplication.feature.task.presentation.TaskScreen
import com.nguyenbaolong.myapplication.feature.task.presentation.TaskViewModel

/**
 * Modern Planner Screen providing a clean, segmented interface between
 * 1. Daily Schedule (Task management by time of day)
 * 2. Core Habits (Recurring task & streak management)
 */
@Composable
fun PlannerScreen(
    taskViewModel: TaskViewModel,
    habitViewModel: HabitViewModel,
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(initialTab.coerceIn(0, 1)) }
    var showAiSheet by remember { mutableStateOf(false) }

    // Update tab if initialTab changes externally
    LaunchedEffect(initialTab) {
        selectedTab = initialTab.coerceIn(0, 1)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar: Segmented Selector + AI Assistant Quick Trigger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf(
                        AppIcons.Schedule to "Daily Schedule",
                        AppIcons.Habits to "Core Habits"
                    )
                    tabs.forEachIndexed { index, (icon, title) ->
                        val isSelected = selectedTab == index
                        Surface(
                            onClick = { selectedTab = index },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                            border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) else null,
                            shadowElevation = if (isSelected) 1.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            Surface(
                onClick = { showAiSheet = true },
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.height(44.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = AppIcons.Sparkle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "AI Plan",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Active Segment Screen
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> TaskScreen(viewModel = taskViewModel)
                1 -> HabitScreen(viewModel = habitViewModel)
            }
        }

        if (showAiSheet) {
            AiProposalSheet(
                taskViewModel = taskViewModel,
                habitViewModel = habitViewModel,
                onDismiss = { showAiSheet = false }
            )
        }
    }
}
