package com.nguyenbaolong.myapplication.feature.stats.presentation

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.Color as AndroidColor
import android.graphics.Paint as AndroidPaint
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.nguyenbaolong.myapplication.core.ui.AppIcons
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityCategory
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Weekly & Monthly Activity Summary Generator & Share Dialog.
 */
@Composable
fun ActivitySummaryModal(
    state: StatsUiState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isWeekly by remember { mutableStateOf(true) }
    var isExporting by remember { mutableStateOf(false) }

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
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }

                    Text(
                        text = "Shareable Summary Card",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.size(40.dp))
                }

                Spacer(Modifier.height(8.dp))

                // Timeframe Selector: Weekly vs Monthly
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            onClick = { isWeekly = true },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isWeekly) MaterialTheme.colorScheme.surface else Color.Transparent,
                            border = if (isWeekly) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)) else null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = AppIcons.Calendar,
                                    contentDescription = null,
                                    tint = if (isWeekly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "This Week",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isWeekly) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isWeekly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            onClick = { isWeekly = false },
                            shape = RoundedCornerShape(10.dp),
                            color = if (!isWeekly) MaterialTheme.colorScheme.surface else Color.Transparent,
                            border = if (!isWeekly) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)) else null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = AppIcons.Calendar,
                                    contentDescription = null,
                                    tint = if (!isWeekly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "This Month",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (!isWeekly) FontWeight.Bold else FontWeight.Medium,
                                    color = if (!isWeekly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Visual Preview Card
                SummaryPreviewCard(state = state, isWeekly = isWeekly)

                Spacer(Modifier.height(18.dp))

                // Action Buttons: Save & Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            isExporting = true
                            try {
                                val bmp = generateSummaryBitmap(context, state, isWeekly)
                                val success = saveBitmapToGallery(context, bmp)
                                if (success) {
                                    Toast.makeText(context, "Saved image to Pictures/EffortLogs!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isExporting = false
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.Download,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Save Image", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            isExporting = true
                            try {
                                val bmp = generateSummaryBitmap(context, state, isWeekly)
                                shareBitmap(context, bmp)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error sharing: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isExporting = false
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Share Card", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryPreviewCard(
    state: StatsUiState,
    isWeekly: Boolean
) {
    val totalMins = if (isWeekly) state.totalMinutesThisWeek else state.totalMinutesThisWeek * 4
    val hours = totalMins / 60
    val mins = totalMins % 60
    val taskPct = if (isWeekly) state.weekCompletionRate else state.todayCompletionRate
    val habitPct = if (state.totalHabitsCount > 0) (state.habitsCompletedToday * 100 / state.totalHabitsCount) else 0
    val bestStreak = state.bestStreakAcrossHabits

    val periodTitle = if (isWeekly) {
        "WEEKLY PERFORMANCE SUMMARY"
    } else {
        "MONTHLY PERFORMANCE SUMMARY"
    }

    val periodSubtitle = if (isWeekly) {
        LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US))
    } else {
        LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US))
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = AppIcons.Bolt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Effort Logs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = if (isWeekly) "7-DAY SPRINT" else "30-DAY REVIEW",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = periodTitle,
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = periodSubtitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(14.dp))

            // 4-Quadrant Metric Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    title = "Task Success",
                    value = "$taskPct%",
                    subtitle = "Completion rate",
                    icon = AppIcons.Done,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "Habit Consistency",
                    value = "$habitPct%",
                    subtitle = "Weekly Target",
                    icon = AppIcons.Goal,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    title = "Effort Logged",
                    value = "${hours}h ${mins}m",
                    subtitle = "Deep work & tasks",
                    icon = AppIcons.Reminder,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "Top Streak",
                    value = "${bestStreak}d",
                    subtitle = "Best chain kept",
                    icon = AppIcons.Streak,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(14.dp))

            // Category Distribution Row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val categories = listOf(
                        "Study" to AppIcons.Study,
                        "Fitness" to AppIcons.Fitness,
                        "Work" to AppIcons.Work,
                        "Personal" to AppIcons.Personal
                    )
                    categories.forEach { (cat, icon) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "100% On-Device & Private • Created with Effort Logs",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MetricTile(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Generates high-res Bitmap (1080 x 1350) for offline saving and sharing.
 */
fun generateSummaryBitmap(
    context: Context,
    state: StatsUiState,
    isWeekly: Boolean
): Bitmap {
    val width = 1080
    val height = 1350
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Background Gradient (Dark royal indigo theme)
    val bgPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        shader = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            AndroidColor.rgb(24, 24, 38),
            AndroidColor.rgb(15, 15, 26),
            Shader.TileMode.CLAMP
        )
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    // Top Card Panel
    val cardPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.argb(220, 36, 36, 56)
    }
    val cardRect = RectF(60f, 60f, width - 60f, height - 60f)
    canvas.drawRoundRect(cardRect, 48f, 48f, cardPaint)

    // Card Border
    val strokePaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.argb(100, 99, 102, 241)
        style = AndroidPaint.Style.STROKE
        strokeWidth = 4f
    }
    canvas.drawRoundRect(cardRect, 48f, 48f, strokePaint)

    // Text Paints
    val titlePaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        textSize = 58f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = AndroidPaint.Align.CENTER
    }

    val subtitlePaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.rgb(165, 180, 252)
        textSize = 32f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = AndroidPaint.Align.CENTER
    }

    val periodTitle = if (isWeekly) "WEEKLY SUMMARY" else "MONTHLY SUMMARY"
    val dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.US))

    canvas.drawText("EFFORT LOGS", width / 2f, 150f, subtitlePaint)
    canvas.drawText(periodTitle, width / 2f, 230f, titlePaint)
    canvas.drawText(dateStr, width / 2f, 290f, subtitlePaint)

    // Divider
    val divPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.argb(80, 255, 255, 255)
        strokeWidth = 2f
    }
    canvas.drawLine(120f, 330f, width - 120f, 330f, divPaint)

    // Metric Grid (4 Blocks)
    val taskPct = if (isWeekly) state.weekCompletionRate else state.todayCompletionRate
    val habitPct = if (state.totalHabitsCount > 0) (state.habitsCompletedToday * 100 / state.totalHabitsCount) else 0
    val totalMins = if (isWeekly) state.totalMinutesThisWeek else state.totalMinutesThisWeek * 4
    val hours = totalMins / 60
    val mins = totalMins % 60
    val streak = state.bestStreakAcrossHabits

    drawMetricBlock(canvas, 100f, 370f, 420f, 220f, "TASK SUCCESS", "$taskPct%", "Completed on schedule")
    drawMetricBlock(canvas, 560f, 370f, 420f, 220f, "HABIT CONSISTENCY", "$habitPct%", "Weekly target rate")
    drawMetricBlock(canvas, 100f, 630f, 420f, 220f, "EFFORT LOGGED", "${hours}h ${mins}m", "Focused activity hours")
    drawMetricBlock(canvas, 560f, 630f, 420f, 220f, "TOP STREAK", "${streak}d", "Longest unbroken chain")

    // Categories Box
    val catBoxPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.argb(160, 48, 48, 76)
    }
    val catRect = RectF(100f, 890f, width - 100f, 1140f)
    canvas.drawRoundRect(catRect, 32f, 32f, catBoxPaint)

    val catHeaderPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        textSize = 34f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = AndroidPaint.Align.LEFT
    }
    canvas.drawText("Activity Highlights", 140f, 950f, catHeaderPaint)

    val catTextPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.rgb(203, 213, 225)
        textSize = 30f
        textAlign = AndroidPaint.Align.LEFT
    }
    canvas.drawText("• Study: Review, coding, homework", 140f, 1010f, catTextPaint)
    canvas.drawText("• Fitness: Gym workout, running, stretching", 140f, 1060f, catTextPaint)
    canvas.drawText("• Work & Personal: Team sync, daily habits", 140f, 1110f, catTextPaint)

    // Watermark
    val footerPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.rgb(148, 163, 184)
        textSize = 28f
        textAlign = AndroidPaint.Align.CENTER
    }
    canvas.drawText("100% On-Device Storage • Zero Cloud Sync • Effort Logs", width / 2f, 1240f, footerPaint)

    return bitmap
}

private fun drawMetricBlock(
    canvas: Canvas,
    x: Float,
    y: Float,
    w: Float,
    h: Float,
    label: String,
    value: String,
    sub: String
) {
    val blockPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.argb(180, 48, 48, 76)
    }
    canvas.drawRoundRect(RectF(x, y, x + w, y + h), 28f, 28f, blockPaint)

    val labelPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.rgb(165, 180, 252)
        textSize = 24f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    canvas.drawText(label, x + 30f, y + 55f, labelPaint)

    val valuePaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        textSize = 52f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    canvas.drawText(value, x + 30f, y + 130f, valuePaint)

    val subPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.rgb(148, 163, 184)
        textSize = 22f
    }
    canvas.drawText(sub, x + 30f, y + 180f, subPaint)
}

/**
 * Saves generated bitmap to device gallery under Pictures/EffortLogs.
 */
fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Boolean {
    val filename = "EffortLogs_Summary_${System.currentTimeMillis()}.png"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EffortLogs")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
    }
    val resolver = context.contentResolver
    val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return false
    resolver.openOutputStream(uri)?.use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.clear()
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)
    }
    return true
}

/**
 * Shares generated bitmap offline via Android's native Intent.ACTION_SEND.
 */
fun shareBitmap(context: Context, bitmap: Bitmap) {
    val cachePath = File(context.cacheDir, "images")
    cachePath.mkdirs()
    val file = File(cachePath, "summary_${System.currentTimeMillis()}.png")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    val authority = "${context.packageName}.fileprovider"
    val uri = FileProvider.getUriForFile(context, authority, file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Activity Performance Card"))
}
