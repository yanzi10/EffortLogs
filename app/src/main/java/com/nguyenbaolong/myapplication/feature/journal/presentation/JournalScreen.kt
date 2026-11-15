package com.nguyenbaolong.myapplication.feature.journal.presentation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nguyenbaolong.myapplication.R
import com.nguyenbaolong.myapplication.core.utils.TimeOfDay
import com.nguyenbaolong.myapplication.core.utils.getCategoryColor
import com.nguyenbaolong.myapplication.core.utils.getCategoryDarkBg
import com.nguyenbaolong.myapplication.core.utils.getCategoryEmoji
import com.nguyenbaolong.myapplication.core.utils.getCategoryName
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityCategory
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityLogEntity
import com.nguyenbaolong.myapplication.feature.journal.data.local.InputType
import com.nguyenbaolong.myapplication.feature.task.presentation.TimeOfDaySelector
import com.nguyenbaolong.myapplication.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun JournalScreen(
    viewModel: JournalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val pendingConfirmation by viewModel.pendingConfirmation.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    var editingLog by remember { mutableStateOf<ActivityLogEntity?>(null) }

    val voiceManager = remember { VoiceInputManager(context) }
    val voiceState by voiceManager.voiceState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            voiceManager.destroy()
        }
    }

    // Google Voice / System Speech Activity Result Launcher
    val systemSpeechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()?.trim()
            if (!spoken.isNullOrBlank()) {
                viewModel.processVoiceInput(spoken, selectedDate)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                systemSpeechLauncher.launch(voiceManager.createSpeechIntent())
            } catch (e: Exception) {
                voiceManager.startListening(
                    onAutoFallback = {
                        try {
                            systemSpeechLauncher.launch(voiceManager.createSpeechIntent())
                        } catch (_: Exception) {}
                    }
                )
            }
        }
    }

    val triggerVoiceInput = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            // Prioritize launching the system Google Voice recognizer directly
            // which works 100% reliably on Xiaomi / MIUI / HyperOS / Samsung / Pixel
            try {
                systemSpeechLauncher.launch(voiceManager.createSpeechIntent())
            } catch (e: Exception) {
                voiceManager.startListening(
                    onAutoFallback = {
                        try {
                            systemSpeechLauncher.launch(voiceManager.createSpeechIntent())
                        } catch (_: Exception) {}
                    }
                )
            }
        }
    }

    // Handle in-app speech recognizer success if triggered
    LaunchedEffect(voiceState) {
        if (voiceState is VoiceState.Success) {
            val text = (voiceState as VoiceState.Success).text
            if (text.isNotBlank()) {
                viewModel.processVoiceInput(text, selectedDate)
                voiceManager.resetState()
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // 1. Date Switcher with Today Shortcut
            ModernDateSwitcher(
                selectedDate = selectedDate,
                onPrevious = { viewModel.setDate(selectedDate.minusDays(1)) },
                onNext = { viewModel.setDate(selectedDate.plusDays(1)) },
                onToday = { viewModel.setDate(LocalDate.now()) }
            )

            Spacer(Modifier.height(10.dp))

            // 2. Input Bar with Direct Voice Trigger (Matching Image 5 Left)
            ModernInputBar(
                textInput = textInput,
                onTextChange = { textInput = it },
                onSubmit = {
                    if (textInput.isNotBlank()) {
                        viewModel.processTextInput(textInput, selectedDate)
                        textInput = ""
                    }
                },
                onMicClick = { triggerVoiceInput() }
            )

            Spacer(Modifier.height(12.dp))

            // 3. Journal Timeline Entries Grouped by Time of Day (Image 5 Left)
            when (val s = uiState) {
                is JournalUiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(strokeWidth = 3.dp, color = MaterialTheme.colorScheme.primary)
                }

                is JournalUiState.Success -> {
                    if (s.logs.isEmpty()) {
                        // Scrollable Empty State to avoid any vertical text clipping ("gạch nửa chữ")
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 80.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            ModernJournalEmptyState(
                                onSampleClick = { sampleText ->
                                    viewModel.processVoiceInput(sampleText, selectedDate)
                                },
                                onMicClick = { triggerVoiceInput() }
                            )
                        }
                    } else {
                        // Group logs by TimeOfDay (Morning, Afternoon, Evening, Other)
                        val morningLogs = s.logs.filter { it.timeOfDay == TimeOfDay.MORNING }
                        val afternoonLogs = s.logs.filter { it.timeOfDay == TimeOfDay.AFTERNOON }
                        val eveningLogs = s.logs.filter { it.timeOfDay == TimeOfDay.EVENING }
                        val otherLogs = s.logs.filter { it.timeOfDay == null }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 96.dp)
                        ) {
                            if (morningLogs.isNotEmpty()) {
                                item {
                                    SectionHeader(title = "MORNING", count = morningLogs.size)
                                }
                                items(morningLogs, key = { it.id }) { log ->
                                    ModernJournalCard(
                                        log = log,
                                        onEdit = { editingLog = log },
                                        onDelete = { viewModel.deleteLog(log) }
                                    )
                                }
                            }

                            if (afternoonLogs.isNotEmpty()) {
                                item {
                                    SectionHeader(title = "AFTERNOON", count = afternoonLogs.size)
                                }
                                items(afternoonLogs, key = { it.id }) { log ->
                                    ModernJournalCard(
                                        log = log,
                                        onEdit = { editingLog = log },
                                        onDelete = { viewModel.deleteLog(log) }
                                    )
                                }
                            }

                            if (eveningLogs.isNotEmpty()) {
                                item {
                                    SectionHeader(title = "EVENING", count = eveningLogs.size)
                                }
                                items(eveningLogs, key = { it.id }) { log ->
                                    ModernJournalCard(
                                        log = log,
                                        onEdit = { editingLog = log },
                                        onDelete = { viewModel.deleteLog(log) }
                                    )
                                }
                            }

                            if (otherLogs.isNotEmpty()) {
                                item {
                                    SectionHeader(title = "OTHER", count = otherLogs.size)
                                }
                                items(otherLogs, key = { it.id }) { log ->
                                    ModernJournalCard(
                                        log = log,
                                        onEdit = { editingLog = log },
                                        onDelete = { viewModel.deleteLog(log) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation Screen (Exact match with Image 4 Left)
    pendingConfirmation?.let { (nlpResult, inputType) ->
        ConfirmationDialog(
            nlpResult = nlpResult,
            inputType = inputType,
            onDismiss = { viewModel.clearPendingConfirmation() },
            onConfirm = { confirmedLog ->
                viewModel.saveConfirmedLog(confirmedLog)
            },
            onReRecordVoice = {
                viewModel.clearPendingConfirmation()
                triggerVoiceInput()
            }
        )
    }

    // Edit Log Dialog (Fixed TimeOfDaySelector - NO afternoon truncation!)
    editingLog?.let { log ->
        EditLogDialog(
            log = log,
            onDismiss = { editingLog = null },
            onConfirm = { updated ->
                viewModel.updateLog(updated)
                editingLog = null
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$title • $count ${if (count == 1) "ENTRY" else "ENTRIES"}",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ModernDateSwitcher(
    selectedDate: LocalDate,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Day",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Surface(
                onClick = onToday,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedDate == LocalDate.now()) "📅 Today • " else "📅 ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("EEE, MMMM d", Locale.US)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(onClick = onNext, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Day",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ModernInputBar(
    textInput: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onMicClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = onTextChange,
                placeholder = {
                    Text(
                        "What did you do today...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            if (textInput.isNotBlank()) {
                IconButton(
                    onClick = onSubmit,
                    modifier = Modifier
                        .size(38.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(6.dp))
            }

            // Glowing Violet Mic Button (Image 5 Left)
            Surface(
                onClick = onMicClick,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mic),
                        contentDescription = "Voice Input",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Modern Journal Card matching Image 5 Left:
 * ● Title
 * "Quote / description in italics"
 * [ Category Badge ]
 * ⏱ Duration   📅 Time   [🎙 voice]    Edit / Delete
 */
@Composable
private fun ModernJournalCard(
    log: ActivityLogEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Title Row with Bullet Point
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = getCategoryColor(log.category),
                        modifier = Modifier.size(8.dp)
                    ) {}
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = log.detectedAction,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
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

            // Quote / raw transcribed input in italics (Image 5 Left)
            if (log.rawInput.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "\"${log.rawInput}\"",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(8.dp))

            // Category Pill Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = getCategoryDarkBg(log.category),
                    border = BorderStroke(1.dp, getCategoryColor(log.category).copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "${getCategoryEmoji(log.category)} ${getCategoryName(log.category)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = getCategoryColor(log.category),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Metadata Row: Duration, Time, Voice Tag
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    log.durationMinutes?.let { mins ->
                        Text(
                            text = "⏱ $mins min",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    log.timeOfDay?.let { tod ->
                        val todEmoji = when (tod) {
                            TimeOfDay.MORNING -> "🌅"
                            TimeOfDay.AFTERNOON -> "☀️"
                            TimeOfDay.EVENING -> "🌙"
                        }
                        Text(
                            text = "$todEmoji ${tod.name.lowercase().replaceFirstChar { it.uppercase() }}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (log.inputType == InputType.VOICE) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "🎙 voice",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Compact, modern empty state that will NEVER be clipped by bottom navigation.
 */
@Composable
private fun ModernJournalEmptyState(
    onSampleClick: (String) -> Unit,
    onMicClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("📖", style = MaterialTheme.typography.titleLarge)
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "No Activities Logged Yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Tap the microphone 🎙️ or type above to record your effort.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Sample logs to test NLP parser:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            val samples = listOf(
                "📚 Studied math for 2 hours this morning",
                "🏃 Gym workout for 45 minutes tonight",
                "💼 Team meeting for one hour this afternoon"
            )

            samples.forEach { sample ->
                Surface(
                    onClick = { onSampleClick(sample.substring(2).trim()) },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sample,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Edit dialog with fixed TimeOfDaySelector preventing any truncation of "Afternoon".
 */
@Composable
private fun EditLogDialog(
    log: ActivityLogEntity,
    onDismiss: () -> Unit,
    onConfirm: (ActivityLogEntity) -> Unit
) {
    var action by remember { mutableStateOf(log.detectedAction) }
    var category by remember { mutableStateOf(log.category) }
    var durationText by remember { mutableStateOf(log.durationMinutes?.toString() ?: "") }
    var timeOfDay by remember { mutableStateOf(log.timeOfDay) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Activity", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = action,
                    onValueChange = { action = it },
                    label = { Text("Action") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                val categories = listOf(
                    ActivityCategory.STUDY to "📚 Study",
                    ActivityCategory.FITNESS to "🏃 Fitness",
                    ActivityCategory.WORK to "💼 Work",
                    ActivityCategory.PERSONAL to "🌿 Personal",
                    ActivityCategory.OTHER to "🎯 Other"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { (cat, label) ->
                        val isSelected = category == cat
                        Surface(
                            onClick = { category = cat },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it.filter { c -> c.isDigit() } },
                    label = { Text("Duration (minutes)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Time of Day", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)

                // NO VERTICAL OVERFLOW & NO TRUNCATION OF AFTERNOON
                TimeOfDaySelector(
                    selectedTimeOfDay = timeOfDay,
                    onSelect = { timeOfDay = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val duration = durationText.toIntOrNull()?.takeIf { it > 0 }
                    onConfirm(
                        log.copy(
                            detectedAction = action.trim(),
                            category = category,
                            durationMinutes = duration,
                            timeOfDay = timeOfDay
                        )
                    )
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
