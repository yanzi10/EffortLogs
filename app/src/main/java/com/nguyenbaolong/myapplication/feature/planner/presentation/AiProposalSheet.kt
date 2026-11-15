package com.nguyenbaolong.myapplication.feature.planner.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nguyenbaolong.myapplication.core.ui.AppIcons
import com.nguyenbaolong.myapplication.core.utils.TimeOfDay
import com.nguyenbaolong.myapplication.feature.habit.presentation.HabitViewModel
import com.nguyenbaolong.myapplication.feature.planner.domain.PlannerAiEngine
import com.nguyenbaolong.myapplication.feature.planner.domain.ProposalItem
import com.nguyenbaolong.myapplication.feature.task.presentation.TaskViewModel

/**
 * Interactive AI Assistant for Task & Habit Planning.
 * Displays generated items as an interactive proposal list where the user
 * can check/uncheck items, adjust time/frequency, and batch insert.
 */
@Composable
fun AiProposalSheet(
    taskViewModel: TaskViewModel,
    habitViewModel: HabitViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var promptInput by remember { mutableStateOf("") }
    var currentProposal by remember {
        mutableStateOf(PlannerAiEngine.generatePlan("Study 30 mins every day this week"))
    }
    var itemsList by remember { mutableStateOf(currentProposal.items) }

    val quickPills = listOf(
        AppIcons.Fitness to "5K Running Routine",
        AppIcons.Study to "Study 30m Daily",
        AppIcons.Personal to "Mindfulness & Sleep",
        AppIcons.Work to "Deep Work Sprint"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = AppIcons.AiBot,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AI Planning Assistant",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Interactive Schedule & Habit Proposal",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Input Box
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = { Text("What goal do you want to plan? (e.g. 5K running, daily coding)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        TextButton(
                            onClick = {
                                if (promptInput.isNotBlank()) {
                                    val plan = PlannerAiEngine.generatePlan(promptInput)
                                    currentProposal = plan
                                    itemsList = plan.items
                                }
                            }
                        ) {
                            Text("Generate", fontWeight = FontWeight.Bold)
                        }
                    }
                )

                Spacer(Modifier.height(8.dp))

                // Quick Idea Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickPills.take(2).forEach { (icon, pillText) ->
                        Surface(
                            onClick = {
                                promptInput = pillText
                                val plan = PlannerAiEngine.generatePlan(pillText)
                                currentProposal = plan
                                itemsList = plan.items
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = pillText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Proposal Plan Card Header
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PROPOSAL PLAN",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.6.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = currentProposal.goalSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        val selectedCount = itemsList.count { it.isSelected }
                        Text(
                            text = "$selectedCount of ${itemsList.size} selected",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Interactive Proposal List
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsList.forEachIndexed { idx, item ->
                        InteractiveProposalItemRow(
                            item = item,
                            onToggleSelect = {
                                itemsList = itemsList.toMutableList().also { list ->
                                    list[idx] = item.copy(isSelected = !item.isSelected)
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Action Buttons
                val checkedCount = itemsList.count { it.isSelected }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val selected = itemsList.filter { it.isSelected }
                            if (selected.isEmpty()) {
                                Toast.makeText(context, "Please select at least 1 item", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            selected.forEach { item ->
                                if (item.isHabit) {
                                    habitViewModel.addHabit(
                                        name = item.title,
                                        cue = null,
                                        targetPerWeek = item.targetCompletionsPerWeek,
                                        frequencyType = item.frequencyType,
                                        targetDays = item.targetDays
                                    )
                                } else {
                                    taskViewModel.addTask(
                                        title = item.title,
                                        timeOfDay = item.timeOfDay
                                    )
                                }
                            }

                            Toast.makeText(context, "Added ${selected.size} items to Schedule & Habits!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .weight(1.8f)
                            .height(48.dp)
                    ) {
                        Text("Add Selected ($checkedCount) ➜", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveProposalItemRow(
    item: ProposalItem,
    onToggleSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (item.isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(
            1.dp,
            if (item.isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleSelect() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )

            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (item.isHabit) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (item.isHabit) AppIcons.Habits else AppIcons.Schedule,
                                contentDescription = null,
                                tint = if (item.isHabit) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = if (item.isHabit) "Habit" else "Task",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = if (item.isHabit) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(Modifier.width(6.dp))

                    val (scheduleIcon, scheduleText) = when {
                        item.isHabit && item.frequencyType == "SPECIFIC_DAYS" -> AppIcons.Calendar to "Mon, Wed, Fri"
                        item.isHabit -> AppIcons.Calendar to "Daily"
                        item.timeOfDay == TimeOfDay.MORNING -> AppIcons.Morning to "Morning"
                        item.timeOfDay == TimeOfDay.AFTERNOON -> AppIcons.Afternoon to "Afternoon"
                        item.timeOfDay == TimeOfDay.EVENING -> AppIcons.Evening to "Evening"
                        else -> AppIcons.PushPin to "Any Time"
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = scheduleIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = scheduleText,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
