package com.nguyenbaolong.myapplication.feature.today.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nguyenbaolong.myapplication.R
import com.nguyenbaolong.myapplication.core.utils.TimeOfDay
import com.nguyenbaolong.myapplication.feature.habit.presentation.HabitUiItem
import com.nguyenbaolong.myapplication.feature.habit.presentation.HabitUiState
import com.nguyenbaolong.myapplication.feature.habit.presentation.HabitViewModel
import com.nguyenbaolong.myapplication.feature.journal.presentation.JournalScreen
import com.nguyenbaolong.myapplication.feature.journal.presentation.JournalViewModel
import com.nguyenbaolong.myapplication.feature.task.data.local.TaskEntity
import com.nguyenbaolong.myapplication.feature.task.presentation.TaskUiState
import com.nguyenbaolong.myapplication.feature.task.presentation.TaskViewModel
import com.nguyenbaolong.myapplication.feature.task.presentation.TimeOfDaySelector
import com.nguyenbaolong.myapplication.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

/**
 * Modern Today Screen (All in English) matching Image 3: "Effort Logs / OFFLINE".
 * Features:
 * 1. Top Bar: Effort Logs • OFFLINE • 100% On-Device
 * 2. Privacy subtitle: 100% Local SQLite Storage • Zero Cloud Connection
 * 3. Daily Completion Card with Circular Ring (e.g. 68% Done), Tasks Left, Habits Kept
 * 4. Section: "Core Habits" with 1-tap check
 * 5. Section: "Daily Schedule" grouped by MORNING, AFTERNOON, EVENING
 * 6. Quick FAB / Voice trigger to record effort
 */
@Composable
fun TodayScreen(
    taskViewModel: TaskViewModel,
    habitViewModel: HabitViewModel,
    journalViewModel: JournalViewModel,
    initialTab: Int = 0
) {
    val taskState by taskViewModel.uiState.collectAsStateWithLifecycle()
    val habitState by habitViewModel.uiState.collectAsStateWithLifecycle()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var showJournalView by remember { mutableStateOf(initialTab == 2) }

    val today = LocalDate.now()
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    val (greeting, greetingEmoji) = when (hour) {
        in 5..11 -> "Good morning..." to "🌅"
        in 12..17 -> "Good afternoon..." to "☀️"
        else -> "Good evening..." to "🌙"
    }

    // English Date Formatter (e.g., "Monday, November 2")
    val formattedDate = today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.US))

    if (showJournalView) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showJournalView = false }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Effort Journal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            JournalScreen(viewModel = journalViewModel)
        }
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showJournalView = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_mic),
                    contentDescription = "Record Effort Voice",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { padding ->
        val currentTasks = (taskState as? TaskUiState.Success)?.tasks ?: emptyList()
        val currentHabits = (habitState as? HabitUiState.Success)?.habits ?: emptyList()

        val totalTasks = currentTasks.size
        val completedTasks = currentTasks.count { it.isCompleted }
        val remainingTasks = totalTasks - completedTasks

        val totalHabits = currentHabits.size
        val completedHabits = currentHabits.count { it.completedToday }

        val totalGoals = totalTasks + totalHabits
        val completedGoals = completedTasks + completedHabits
        val completionPercentage = if (totalGoals > 0) (completedGoals * 100) / totalGoals else 0

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Bar: Effort Logs OFFLINE • 100% On-Device (Image 3)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier.size(26.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🛡️", fontSize = 13.sp)
                            }
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Effort Logs",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.width(8.dp))
                        // Mint Green OFFLINE Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🟢", fontSize = 8.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "OFFLINE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    // On-Device Chip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = "● 100% On-Device",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 2. Greeting Header & Privacy Subtitle Banner (Image 3)
            item {
                Column {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "$greeting $greetingEmoji",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(8.dp))

                    // Soft Privacy Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🛡️", fontSize = 12.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "100% Local SQLite Storage • Zero Cloud Connection",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // 3. Today's Progress Card with Circular Ring (Image 3)
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = "Daily Target",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Today's Progress",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "$completedGoals of $totalGoals goals completed",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(12.dp))

                            // 2 Pill Counters
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("✓", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "$remainingTasks Tasks Left",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🔥", fontSize = 10.sp)
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "$completedHabits Habits Kept",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.width(14.dp))

                        // Circular Progress Ring (Image 3)
                        ModernTodayRing(percentage = completionPercentage)
                    }
                }
            }

            // 4. Section: Core Habits (Image 3)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔁", fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Core Habits",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = "View All >",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showJournalView = true }
                    )
                }
            }

            if (currentHabits.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No active habits yet. Add your first habit in Profile to build consistency.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(currentHabits.take(3), key = { it.habit.id }) { item ->
                    ModernCoreHabitRow(
                        item = item,
                        onToggle = { habitViewModel.toggleToday(item.habit.id) }
                    )
                }
            }

            // 5. Section: Daily Schedule (Image 3)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📋", fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Daily Schedule",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Surface(
                        onClick = { showAddTaskDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("+", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Add task",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Tasks Grouped by Morning, Afternoon, Evening (Image 3)
            val morningTasks = currentTasks.filter { it.timeOfDay == TimeOfDay.MORNING }
            val afternoonTasks = currentTasks.filter { it.timeOfDay == TimeOfDay.AFTERNOON }
            val eveningTasks = currentTasks.filter { it.timeOfDay == TimeOfDay.EVENING }
            val unscheduledTasks = currentTasks.filter { it.timeOfDay == null }

            // ☀️ MORNING
            item {
                TimeSlotHeader(title = "MORNING", emoji = "☀️")
            }
            if (morningTasks.isEmpty()) {
                item {
                    EmptyTimeSlotRow(text = "No morning tasks scheduled")
                }
            } else {
                items(morningTasks, key = { it.id }) { task ->
                    ModernScheduleTaskRow(
                        task = task,
                        onToggle = { taskViewModel.toggleComplete(task) },
                        onEdit = { editingTask = task },
                        onDelete = { taskViewModel.deleteTask(task) }
                    )
                }
            }

            // ☀️ AFTERNOON
            item {
                TimeSlotHeader(title = "AFTERNOON", emoji = "☀️")
            }
            if (afternoonTasks.isEmpty()) {
                item {
                    EmptyTimeSlotRow(text = "No afternoon tasks scheduled")
                }
            } else {
                items(afternoonTasks, key = { it.id }) { task ->
                    ModernScheduleTaskRow(
                        task = task,
                        onToggle = { taskViewModel.toggleComplete(task) },
                        onEdit = { editingTask = task },
                        onDelete = { taskViewModel.deleteTask(task) }
                    )
                }
            }

            // 🌙 EVENING
            item {
                TimeSlotHeader(title = "EVENING", emoji = "🌙")
            }
            if (eveningTasks.isEmpty() && unscheduledTasks.isEmpty()) {
                item {
                    EmptyTimeSlotRow(text = "No evening tasks scheduled")
                }
            } else {
                items(eveningTasks + unscheduledTasks, key = { it.id }) { task ->
                    ModernScheduleTaskRow(
                        task = task,
                        onToggle = { taskViewModel.toggleComplete(task) },
                        onEdit = { editingTask = task },
                        onDelete = { taskViewModel.deleteTask(task) }
                    )
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddScheduleTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, timeOfDay ->
                taskViewModel.addTask(title, timeOfDay)
                showAddTaskDialog = false
            }
        )
    }

    editingTask?.let { task ->
        EditScheduleTaskDialog(
            task = task,
            onDismiss = { editingTask = null },
            onConfirm = { newTitle, newTimeOfDay ->
                taskViewModel.updateTask(task, newTitle, newTimeOfDay)
                editingTask = null
            }
        )
    }
}

@Composable
private fun ModernTodayRing(percentage: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "ring"
    )

    Box(
        modifier = Modifier.size(76.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(72.dp)) {
            val stroke = 8.dp.toPx()
            // Track
            drawArc(
                color = Color(0xFFEEF2FF),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            // Progress
            drawArc(
                color = Color(0xFF4F46E5),
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4F46E5)
            )
            Text(
                text = "Done",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Core Habit Row matching Image 3:
 * [ Icon in square ] Name, Cue, Streak | Green Circle Check
 */
@Composable
private fun ModernCoreHabitRow(
    item: HabitUiItem,
    onToggle: () -> Unit
) {
    val habitEmoji = when {
        item.habit.name.contains("water", ignoreCase = true) || item.habit.name.contains("drink", ignoreCase = true) -> "💧"
        item.habit.name.contains("read", ignoreCase = true) || item.habit.name.contains("book", ignoreCase = true) -> "📖"
        item.habit.name.contains("meditat", ignoreCase = true) || item.habit.name.contains("breath", ignoreCase = true) -> "🧘"
        item.habit.name.contains("gym", ignoreCase = true) || item.habit.name.contains("run", ignoreCase = true) || item.habit.name.contains("workout", ignoreCase = true) -> "🏃"
        else -> "🎯"
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(habitEmoji, fontSize = 20.sp)
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.habit.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    item.habit.cue?.let { cue ->
                        Text(
                            text = "⏰ Cue: $cue",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔥", fontSize = 10.sp)
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = if (item.currentStreak > 0) "${item.currentStreak}-day Streak" else "Started today",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = if (item.currentStreak > 0) Color(0xFFD97706) else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Green Check Circle (Image 3)
            Surface(
                onClick = onToggle,
                shape = CircleShape,
                color = if (item.completedToday) MaterialTheme.colorScheme.secondary else Color.Transparent,
                border = if (!item.completedToday) BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline) else null,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (item.completedToday) {
                        Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeSlotHeader(title: String, emoji: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 12.sp)
        Spacer(Modifier.width(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyTimeSlotRow(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

/**
 * Schedule Task Row matching Image 3:
 * [✓] Title  Time
 */
@Composable
private fun ModernScheduleTaskRow(
    task: TaskEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle checkbox
                Surface(
                    shape = CircleShape,
                    color = if (task.isCompleted) Color(0xFF10B981) else Color.Transparent,
                    border = if (!task.isCompleted) BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline) else null,
                    modifier = Modifier.size(22.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (task.isCompleted) {
                            Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(Modifier.width(10.dp))

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = { showMenu = false; onEdit() },
                        leadingIcon = { Icon(Icons.Default.Edit, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = { showMenu = false; onDelete() },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddScheduleTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, TimeOfDay?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedTimeOfDay by remember { mutableStateOf<TimeOfDay?>(TimeOfDay.MORNING) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    placeholder = { Text("e.g., Read research paper, Team sync...") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Time of Day", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                TimeOfDaySelector(
                    selectedTimeOfDay = selectedTimeOfDay,
                    onSelect = { selectedTimeOfDay = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onConfirm(title.trim(), selectedTimeOfDay) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun EditScheduleTaskDialog(
    task: TaskEntity,
    onDismiss: () -> Unit,
    onConfirm: (String, TimeOfDay?) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var selectedTimeOfDay by remember { mutableStateOf(task.timeOfDay) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Time of Day", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                TimeOfDaySelector(
                    selectedTimeOfDay = selectedTimeOfDay,
                    onSelect = { selectedTimeOfDay = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onConfirm(title.trim(), selectedTimeOfDay) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
