package com.nguyenbaolong.myapplication.feature.onboarding.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class OnboardingStep(
    val stepIndex: Int,
    val iconEmoji: String,
    val badge: String,
    val title: String,
    val description: String,
    val highlightPills: List<String>,
    val accentColor: Color
)

val onboardingSteps = listOf(
    OnboardingStep(
        stepIndex = 0,
        iconEmoji = "⚡",
        badge = "Overview & Focus",
        title = "Your Daily Focus Command Center",
        description = "Effort Logs keeps your day crystal clear with progressive disclosure. See your daily focus ring, upcoming priority tasks, and habit streaks at a single glance without visual noise.",
        highlightPills = listOf("📊 Bento Dashboard", "⚡ 1-Tap Quick Actions", "🌱 Live Companion Progress"),
        accentColor = Color(0xFF4F46E5)
    ),
    OnboardingStep(
        stepIndex = 1,
        iconEmoji = "📋",
        badge = "Two Distinct Systems",
        title = "Daily Schedule vs. Core Habits",
        description = "Never confuse one-off chores with recurring routines again. Daily Schedule organizes tasks by morning, afternoon, and evening. Core Habits tracks recurring routines with smart rest-day streaks.",
        highlightPills = listOf("🌅 Time-of-Day Schedule", "🔥 Rest-Day Streak Protection", "📅 Flexible Weekday Routines"),
        accentColor = Color(0xFF0284C7)
    ),
    OnboardingStep(
        stepIndex = 2,
        iconEmoji = "🎙️",
        badge = "100% On-Device Privacy",
        title = "Offline Personal Voice Journal",
        description = "Record what you accomplished, how long it took, and how you felt. On-device offline NLP extracts activities instantly with zero cloud latency and guaranteed offline privacy.",
        highlightPills = listOf("🔒 Zero Cloud Transmission", "⚡ Offline Speech-to-Text", "🧠 Rule-based Activity Parsing"),
        accentColor = Color(0xFF10B981)
    ),
    OnboardingStep(
        stepIndex = 3,
        iconEmoji = "✨",
        badge = "Gamified Growth",
        title = "AI Assistant & Goal Companion",
        description = "Set your long-term North Star goal. Let the interactive AI assistant suggest actionable schedules and habits. Watch your Effort Sprout evolve into a legendary Cosmic Guardian as you earn XP!",
        highlightPills = listOf("✨ Interactive Proposals", "🌟 4 Companion Growth Stages", "📸 Exportable Performance Cards"),
        accentColor = Color(0xFF8B5CF6)
    )
)

/**
 * Skippable First-Time Onboarding Walkthrough & Settings Tutorial Replay.
 */
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStepIndex by remember { mutableIntStateOf(0) }
    val step = onboardingSteps[currentStepIndex]
    val isLastStep = currentStepIndex == onboardingSteps.size - 1

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step indicator badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "Step ${currentStepIndex + 1} of ${onboardingSteps.size}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                // Clear Skip Button
                TextButton(
                    onClick = onFinish,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Pager Indicators (Expanding pill for current step)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        onboardingSteps.forEachIndexed { index, item ->
                            val isSelected = index == currentStepIndex
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .height(8.dp)
                                    .width(if (isSelected) 28.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) item.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }

                    // Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (currentStepIndex > 0) {
                            OutlinedButton(
                                onClick = { currentStepIndex-- },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "Back",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (isLastStep) {
                                    onFinish()
                                } else {
                                    currentStepIndex++
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = step.accentColor
                            ),
                            modifier = Modifier
                                .weight(if (currentStepIndex > 0) 1.5f else 1f)
                                .height(50.dp)
                        ) {
                            Text(
                                text = if (isLastStep) "Get Started 🚀" else "Next ➜",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                if (targetState.stepIndex > initialState.stepIndex) {
                    (slideInHorizontally { width -> width } + fadeIn(tween(300)))
                        .togetherWith(slideOutHorizontally { width -> -width } + fadeOut(tween(300)))
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn(tween(300)))
                        .togetherWith(slideOutHorizontally { width -> width } + fadeOut(tween(300)))
                }
            },
            label = "onboardingSlide",
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { currentItem ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large Hero Icon Box
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    currentItem.accentColor.copy(alpha = 0.25f),
                                    currentItem.accentColor.copy(alpha = 0.05f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentItem.iconEmoji,
                        fontSize = 54.sp
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = currentItem.accentColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = currentItem.badge,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = currentItem.accentColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Title
                Text(
                    text = currentItem.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(12.dp))

                // Description
                Text(
                    text = currentItem.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(Modifier.height(24.dp))

                // Feature Highlights Pills
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    currentItem.highlightPills.forEach { pill ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, currentItem.accentColor.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("✓", fontWeight = FontWeight.Bold, color = currentItem.accentColor)
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = pill,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
