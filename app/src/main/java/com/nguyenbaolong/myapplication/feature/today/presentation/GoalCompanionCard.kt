package com.nguyenbaolong.myapplication.feature.today.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyenbaolong.myapplication.core.ui.AppIcons
import kotlin.math.cos
import kotlin.math.sin

data class CompanionStage(
    val stageNumber: Int,
    val name: String,
    val icon: ImageVector,
    val stageTitle: String,
    val minXp: Int,
    val maxXp: Int,
    val quote: String,
    val accentColor: Color
)

fun getCompanionStage(xp: Int): CompanionStage {
    return when {
        xp < 60 -> CompanionStage(
            stageNumber = 1,
            name = "Effort Sprout",
            icon = AppIcons.Meditation,
            stageTitle = "Stage 1 • Seedling",
            minXp = 0,
            maxXp = 60,
            quote = "Every small action plants a seed for greatness.",
            accentColor = Color(0xFF10B981)
        )
        xp < 160 -> CompanionStage(
            stageNumber = 2,
            name = "Growing Sapling",
            icon = AppIcons.Trophy,
            stageTitle = "Stage 2 • Sapling",
            minXp = 60,
            maxXp = 160,
            quote = "Your roots are deepening. Consistency is taking hold!",
            accentColor = Color(0xFF059669)
        )
        xp < 320 -> CompanionStage(
            stageNumber = 3,
            name = "Blooming Tree",
            icon = AppIcons.AiBot,
            stageTitle = "Stage 3 • Blooming",
            minXp = 160,
            maxXp = 320,
            quote = "Daily effort blooms into incredible habits and results!",
            accentColor = Color(0xFFEC4899)
        )
        else -> CompanionStage(
            stageNumber = 4,
            name = "Cosmic Guardian",
            icon = AppIcons.Goal,
            stageTitle = "Stage 4 • Legendary Master",
            minXp = 320,
            maxXp = 500,
            quote = "Unstoppable momentum! A shining beacon of dedication.",
            accentColor = Color(0xFF8B5CF6)
        )
    }
}

/**
 * Goal Evolution Companion (Gamification for Big Goals).
 * Renders an evolving companion linked to the user's primary long-term goal.
 * Displays current growth stage, XP progression, motivational cues, and AI planner trigger.
 */
@Composable
fun GoalCompanionCard(
    primaryGoal: String,
    companionName: String,
    companionXp: Int,
    onUpdateGoal: (newGoal: String, newName: String) -> Unit,
    onOpenAiPlanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stage = remember(companionXp) { getCompanionStage(companionXp) }

    val rawProgress = if (stage.stageNumber == 4) {
        ((companionXp - stage.minXp).toFloat() / (stage.maxXp - stage.minXp)).coerceIn(0f, 1f)
    } else {
        ((companionXp - stage.minXp).toFloat() / (stage.maxXp - stage.minXp)).coerceIn(0f, 1f)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 800),
        label = "companionXpProgress"
    )

    var showEditDialog by remember { mutableStateOf(false) }

    val avatarScale = remember { Animatable(1f) }
    val confettiProgress = remember { Animatable(0f) }
    var previousStage by remember { mutableIntStateOf(stage.stageNumber) }
    var showLevelUpBanner by remember { mutableStateOf(false) }

    LaunchedEffect(companionXp) {
        if (companionXp > 0) {
            val hasLeveledUp = stage.stageNumber > previousStage
            if (hasLeveledUp) {
                showLevelUpBanner = true
                confettiProgress.snapTo(0f)
                confettiProgress.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
                avatarScale.animateTo(1.35f, tween(150, easing = FastOutSlowInEasing))
                avatarScale.animateTo(0.9f, tween(120, easing = FastOutSlowInEasing))
                avatarScale.animateTo(1.1f, tween(100, easing = FastOutSlowInEasing))
                avatarScale.animateTo(1.0f, tween(100, easing = FastOutSlowInEasing))
            } else {
                avatarScale.animateTo(1.15f, tween(100, easing = FastOutSlowInEasing))
                avatarScale.animateTo(1.0f, tween(100, easing = FastOutSlowInEasing))
            }
            previousStage = stage.stageNumber
        }
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, stage.accentColor.copy(alpha = 0.35f)),
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Level Up Celebration Banner
            if (showLevelUpBanner) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = stage.accentColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, stage.accentColor.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = AppIcons.Celebrate,
                                contentDescription = null,
                                tint = stage.accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "LEVEL UP! Evolved to ${stage.name}!",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = stage.accentColor
                            )
                        }
                        Text(
                            text = "✕",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = stage.accentColor,
                            modifier = Modifier.clickable { showLevelUpBanner = false }
                        )
                    }
                }
            }

            // Header: Avatar, Name, Goal & Edit Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Companion Avatar Box with Confetti
                Box(
                    modifier = Modifier.size(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (confettiProgress.value in 0.01f..0.99f) {
                        val progress = confettiProgress.value
                        Canvas(modifier = Modifier.size(80.dp)) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val maxRadius = 38.dp.toPx()
                            val currentRadius = progress * maxRadius
                            val alpha = (1f - progress).coerceIn(0f, 1f)
                            val colors = listOf(stage.accentColor, Color(0xFFF59E0B), Color(0xFFEC4899), Color(0xFF10B981))
                            for (i in 0 until 12) {
                                val angle = (i.toFloat() / 12f) * (2 * Math.PI)
                                val px = center.x + (currentRadius * cos(angle)).toFloat()
                                val py = center.y + (currentRadius * sin(angle)).toFloat()
                                drawCircle(
                                    color = colors[i % colors.size].copy(alpha = alpha),
                                    radius = 3.dp.toPx() * (1f - progress * 0.3f),
                                    center = Offset(px, py)
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .scale(avatarScale.value)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        stage.accentColor.copy(alpha = 0.25f),
                                        stage.accentColor.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = stage.icon,
                            contentDescription = null,
                            tint = stage.accentColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = stage.accentColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = stage.stageTitle,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                fontWeight = FontWeight.Bold,
                                color = stage.accentColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "• $companionName",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showEditDialog = true }
                    ) {
                        Text(
                            text = primaryGoal,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Goal",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // XP Progress Bar & Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stage.name} Evolution",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$companionXp / ${stage.maxXp} XP",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = stage.accentColor
                )
            }

            Spacer(Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = stage.accentColor,
                trackColor = stage.accentColor.copy(alpha = 0.15f)
            )

            Spacer(Modifier.height(12.dp))

            // Speech Bubble / Motivation Quote
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = AppIcons.Chat,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "\"${stage.quote}\"",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Action Row: AI Assistant Trigger
            FilledTonalButton(
                onClick = onOpenAiPlanner,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = AppIcons.AiBot,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Plan Goal with AI Assistant ➜",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        var goalText by remember { mutableStateOf(primaryGoal) }
        var nameText by remember { mutableStateOf(companionName) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "Customize Long-term Goal",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Set your main north star. Your companion evolves as you complete daily tasks & habits towards it.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = goalText,
                        onValueChange = { goalText = it },
                        label = { Text("Primary Goal") },
                        placeholder = { Text("e.g. Master Kotlin, Run Marathon") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nameText,
                        onValueChange = { nameText = it },
                        label = { Text("Companion Nickname") },
                        placeholder = { Text("e.g. Effort Sprout") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateGoal(goalText, nameText)
                        showEditDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
