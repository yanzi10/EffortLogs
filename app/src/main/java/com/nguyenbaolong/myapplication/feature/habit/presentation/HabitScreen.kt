package com.nguyenbaolong.myapplication.feature.habit.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nguyenbaolong.myapplication.feature.habit.data.local.HabitEntity
import com.nguyenbaolong.myapplication.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * Modern Habits Screen matching Image 5 Right.
 * Features:
 * 1. "This week" 7-day circular tracker header (M T W T F S S)
 * 2. "ACTIVE HABITS • X" section header
 * 3. Habit cards with icon box, name, cue, streak 🔥 and 7-day completion pills
 * 4. Glowing violet FAB (+)
 */
@Composable
fun HabitScreen(
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<HabitUiItem?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            when (val s = state) {
                is HabitUiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(strokeWidth = 3.dp, color = MaterialTheme.colorScheme.primary)
                }

                is HabitUiState.Success -> {
                    val today = LocalDate.now()
                    val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 96.dp)
                    ) {
                        // 1. "This week" 7-Day Completion Tracker (Image 5 Right)
                        item {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "This week",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.height(10.dp))

                                    val weekdays = listOf("M", "T", "W", "T", "F", "S", "S")

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        weekdays.forEachIndexed { idx, label ->
                                            val dayDate = monday.plusDays(idx.toLong())
                                            val isPastOrToday = !dayDate.isAfter(today)
                                            val isCompletedAnyHabit = s.habits.any { it.completionDates.contains(dayDate) }
                                            val isTodayDate = dayDate == today

                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.width(38.dp)
                                            ) {
                                                Text(
                                                    text = label,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = if (isTodayDate) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isTodayDate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(Modifier.height(6.dp))

                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = when {
                                                        isCompletedAnyHabit -> MaterialTheme.colorScheme.primary
                                                        isPastOrToday -> MaterialTheme.colorScheme.surfaceVariant
                                                        else -> Color.Transparent
                                                    },
                                                    border = if (!isCompletedAnyHabit && isPastOrToday)
                                                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                                    else null,
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = if (isCompletedAnyHabit) "✓" else "-",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp,
                                                            color = if (isCompletedAnyHabit) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Active Habits Section Header (Image 5 Right)
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ACTIVE HABITS • ${s.habits.size}",
                                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // 3. Habit Cards
                        if (s.habits.isEmpty()) {
                            item {
                                ModernHabitEmptyState(onAddHabit = { showAddDialog = true })
                            }
                        } else {
                            items(s.habits, key = { it.habit.id }) { item ->
                                ModernHabitCard(
                                    item = item,
                                    onToggle = { viewModel.toggleToday(item.habit.id) },
                                    onEdit = { editingItem = item },
                                    onDelete = { viewModel.deleteHabit(item.habit) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button (Glowing violet plus button matching Image 5 Right)
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 20.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Habit", modifier = Modifier.size(26.dp))
        }
    }

    if (showAddDialog) {
        HabitDialog(
            title = "New Daily Habit",
            initialName = "",
            initialCue = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, cue ->
                viewModel.addHabit(name, cue.ifBlank { null }, 7)
                showAddDialog = false
            }
        )
    }

    editingItem?.let { item ->
        HabitDialog(
            title = "Edit Habit",
            initialName = item.habit.name,
            initialCue = item.habit.cue ?: "",
            onDismiss = { editingItem = null },
            onConfirm = { name, cue ->
                viewModel.updateHabit(item.habit, name, cue.ifBlank { null })
                editingItem = null
            }
        )
    }
}

/**
 * Modern Habit Card matching Image 5 Right:
 * [ Icon ] Name, Cue | Streak 🔥
 * Sub-row: M T W T F S S with individual day completion pills!
 */
@Composable
private fun ModernHabitCard(
    item: HabitUiItem,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val today = LocalDate.now()
    val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    val habitEmoji = when {
        item.habit.name.contains("water", ignoreCase = true) || item.habit.name.contains("uống", ignoreCase = true) -> "💧"
        item.habit.name.contains("meditat", ignoreCase = true) || item.habit.name.contains("thiền", ignoreCase = true) -> "🧘"
        item.habit.name.contains("gym", ignoreCase = true) || item.habit.name.contains("chạy", ignoreCase = true) || item.habit.name.contains("tập", ignoreCase = true) -> "🏋️"
        item.habit.name.contains("read", ignoreCase = true) || item.habit.name.contains("study", ignoreCase = true) || item.habit.name.contains("học", ignoreCase = true) -> "📖"
        else -> "🎯"
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (item.completedToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Icon box, Habit Name + Cue, Streak Count & Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rounded Square Icon Container (Image 5 Right)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(habitEmoji, fontSize = 22.sp)
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = item.habit.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        item.habit.cue?.let { cue ->
                            Text(
                                text = "Cue: $cue",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Streak pill & Menu
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (item.currentStreak > 0) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${item.currentStreak}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.width(2.dp))
                            Text("🔥", fontSize = 12.sp)
                            Spacer(Modifier.width(2.dp))
                            Text("day", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
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

            Spacer(Modifier.height(12.dp))

            // Sub-row: 7-day week progress pills M T W T F S S (Image 5 Right)
            val weekdays = listOf("M", "T", "W", "T", "F", "S", "S")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekdays.forEachIndexed { idx, label ->
                    val dayDate = monday.plusDays(idx.toLong())
                    val isCompleted = item.completionDates.contains(dayDate)
                    val isToday = dayDate == today

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when {
                            isCompleted -> MaterialTheme.colorScheme.primary
                            isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        },
                        border = if (isToday && !isCompleted)
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                        else null,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .height(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                fontWeight = if (isCompleted || isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCompleted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernHabitEmptyState(onAddHabit: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🔥", style = MaterialTheme.typography.titleLarge)
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Build Daily Routines",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Add micro-habits and trigger cues to build long-lasting consistency.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onAddHabit,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Create First Habit", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HabitDialog(
    title: String,
    initialName: String,
    initialCue: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var cue by remember { mutableStateOf(initialCue) }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text("Habit Name") },
                    placeholder = { Text("e.g., Drink water, Meditate") },
                    isError = nameError,
                    supportingText = { if (nameError) Text("Name cannot be empty") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = cue,
                    onValueChange = { cue = it },
                    label = { Text("Cue / Context (Optional)") },
                    placeholder = { Text("e.g., Right after morning coffee") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else {
                        onConfirm(name.trim(), cue.trim())
                    }
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
