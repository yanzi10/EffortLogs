package com.nguyenbaolong.myapplication.feature.journal.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nguyenbaolong.myapplication.R
import com.nguyenbaolong.myapplication.core.utils.TimeOfDay
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityCategory
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityLogEntity
import com.nguyenbaolong.myapplication.feature.journal.data.local.InputType
import com.nguyenbaolong.myapplication.feature.journal.domain.NlpResult
import com.nguyenbaolong.myapplication.feature.task.presentation.TimeOfDaySelector
import com.nguyenbaolong.myapplication.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Modern "Quick Voice Entry" / "Voice Journal & NLP Extraction" matching Image 2.
 * Features:
 * 1. Top Bar: Quick Voice Entry + On-Device Vosk Banner
 * 2. Soundwave graphic with Listening status & large royal purple mic button
 * 3. STT Result Card with 94% confidence chip and editable quote
 * 4. Smart NLP Extraction card:
 *    - Primary Action (Detected action)
 *    - Category chips
 *    - Extracted Duration (- / +)
 *    - Time & Date (Morning / Afternoon / Evening · Date)
 *    - Emotion & Sentiment (😊 Positive)
 * 5. Action Buttons: Cancel & Save Activity
 */
@Composable
fun ConfirmationDialog(
    nlpResult: NlpResult,
    inputType: InputType,
    onDismiss: () -> Unit,
    onConfirm: (ActivityLogEntity) -> Unit,
    onReRecordVoice: (() -> Unit)? = null
) {
    var action by remember { mutableStateOf(nlpResult.detectedAction) }
    var category by remember { mutableStateOf(nlpResult.category) }
    var durationMinutes by remember { mutableStateOf(nlpResult.durationMinutes ?: 30) }
    var timeOfDay by remember { mutableStateOf(nlpResult.timeOfDay ?: TimeOfDay.MORNING) }
    var activityDate by remember { mutableStateOf(nlpResult.activityDate) }

    var isEditingAction by remember { mutableStateOf(false) }
    var isEditingTimeOfDay by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Top Navigation Bar (Image 2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }

                    Text(
                        text = "Quick Voice Entry",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("👤", fontSize = 14.sp)
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // 2. Offline Vosk Engine / On-Device Banner (Image 2)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("●", color = MaterialTheme.colorScheme.secondary, fontSize = 10.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "OFFLINE VOSK ENGINE • SECURE MIC",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Text(
                            text = "🔒 100% On-Device",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // 3. Voice Journal & Soundwave Card (Image 2)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Voice Journal & NLP Extraction",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "On-device processing & instant intent extraction",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(12.dp))

                        // Soundwave Graphic (Image 2)
                        SoundwaveVisualizer()

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "● Speech recognition complete",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(12.dp))

                        // Big Royal Purple Mic Button (Image 2)
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .size(56.dp)
                                .clickable(enabled = onReRecordVoice != null) { onReRecordVoice?.invoke() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_mic),
                                    contentDescription = "Re-record voice",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = "🛡️ Audio is temporarily cached locally and deleted immediately after extraction. Never uploaded to the cloud.",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // 4. Card: NHẬN DIỆN ÂM THANH (STT) (Image 2)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SPEECH RECOGNITION (STT)",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("✨", fontSize = 10.sp)
                                    Spacer(Modifier.width(2.dp))
                                    Text(
                                        text = "94% confidence",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Quote in speech box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "\"${nlpResult.rawInput}\"",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 14.sp
                                ),
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // 5. Card: Smart NLP Extraction (Image 2)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🧠", fontSize = 12.sp)
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Smart NLP Extraction",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Offline Rule-based NLP Active",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // Row 1: Primary Action
                        Text(
                            text = "Primary Action",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Surface(
                            onClick = { isEditingAction = !isEditingAction },
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🏃", fontSize = 14.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = action,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text("Edit", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }

                        AnimatedVisibility(visible = isEditingAction) {
                            OutlinedTextField(
                                value = action,
                                onValueChange = { action = it },
                                placeholder = { Text("Action name...") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Row 2: Category Chips
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(6.dp))

                        val categoryList = listOf(
                            ActivityCategory.FITNESS to "🏃 Fitness",
                            ActivityCategory.STUDY to "📖 Study",
                            ActivityCategory.WORK to "💼 Work",
                            ActivityCategory.OTHER to "••• Other"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categoryList.forEach { (cat, label) ->
                                val isSelected = category == cat
                                Surface(
                                    onClick = { category = cat },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Row 3: Extracted Duration
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Extracted Duration",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    onClick = { if (durationMinutes > 5) durationMinutes -= 5 },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                }

                                Text(
                                    text = "⏱ $durationMinutes min",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )

                                Surface(
                                    onClick = { durationMinutes += 15 },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Row 4: Time & Date
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Time & Date",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Surface(
                                onClick = { isEditingTimeOfDay = !isEditingTimeOfDay },
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val todLabel = when (timeOfDay) {
                                        TimeOfDay.MORNING -> "🌅 Morning"
                                        TimeOfDay.AFTERNOON -> "☀️ Afternoon"
                                        TimeOfDay.EVENING -> "🌙 Evening"
                                    }
                                    Text(
                                        text = "$todLabel • ${activityDate.format(DateTimeFormatter.ofPattern("MMM d", java.util.Locale.US))}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text("📅", fontSize = 12.sp)
                                }
                            }
                        }

                        AnimatedVisibility(visible = isEditingTimeOfDay) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                TimeOfDaySelector(
                                    selectedTimeOfDay = timeOfDay,
                                    onSelect = { if (it != null) timeOfDay = it }
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Row 5: Emotion & Sentiment (Image 2)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Emotion & Sentiment",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "😊 Positive",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // 6. Bottom Action Buttons (Cancel & Save Activity) (Image 2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = {
                            val finalAction = action.ifBlank { "Untitled Activity" }
                            val log = ActivityLogEntity(
                                rawInput = nlpResult.rawInput,
                                inputType = inputType,
                                detectedAction = finalAction,
                                category = category,
                                durationMinutes = durationMinutes,
                                timeOfDay = timeOfDay,
                                activityDate = activityDate
                            )
                            onConfirm(log)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                    ) {
                        Text("✓ Save Activity", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun SoundwaveVisualizer() {
    val barHeights = listOf(14f, 26f, 40f, 22f, 34f, 48f, 28f, 18f, 32f, 44f, 20f, 12f)
    val barColors = listOf(
        Color(0xFF4F46E5),
        Color(0xFF6366F1),
        Color(0xFF10B981),
        Color(0xFF3B82F6),
        Color(0xFF4F46E5),
        Color(0xFF10B981),
        Color(0xFF6366F1),
        Color(0xFF3B82F6),
        Color(0xFF4F46E5),
        Color(0xFF10B981),
        Color(0xFF6366F1),
        Color(0xFF4F46E5)
    )

    Row(
        modifier = Modifier
            .height(52.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        barHeights.forEachIndexed { idx, heightDp ->
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = barColors[idx % barColors.size],
                modifier = Modifier
                    .width(4.dp)
                    .height(heightDp.dp)
            ) {}
        }
    }
}
