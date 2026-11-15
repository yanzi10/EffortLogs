package com.nguyenbaolong.myapplication.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Modern Animated Completion Checkmark with:
 * 1. Scale bounce transition
 * 2. Morphing checkmark reveal
 * 3. Radiant particle / confetti burst
 */
@Composable
fun AnimatedCompletionCheck(
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 26.dp,
    checkedColor: Color = MaterialTheme.colorScheme.primary,
    checkmarkColor: Color = Color.White,
    shape: Shape = CircleShape
) {
    val scaleAnim = remember { Animatable(1f) }
    val checkmarkScale = remember { Animatable(if (checked) 1f else 0f) }
    val checkmarkRotation = remember { Animatable(if (checked) 0f else -25f) }
    val particleProgress = remember { Animatable(0f) }

    LaunchedEffect(checked) {
        if (checked) {
            // Trigger 1: Scale bounce
            scaleAnim.animateTo(0.78f, animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing))
            scaleAnim.animateTo(1.28f, animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing))
            scaleAnim.animateTo(1.0f, animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing))
        } else {
            scaleAnim.snapTo(1f)
        }
    }

    LaunchedEffect(checked) {
        if (checked) {
            // Trigger 2: Checkmark reveal and rotation
            checkmarkRotation.snapTo(-25f)
            checkmarkScale.snapTo(0f)
            checkmarkRotation.animateTo(0f, animationSpec = tween(220, easing = FastOutSlowInEasing))
            checkmarkScale.animateTo(1f, animationSpec = tween(200, easing = FastOutSlowInEasing))
        } else {
            checkmarkScale.animateTo(0f, animationSpec = tween(120, easing = LinearEasing))
        }
    }

    LaunchedEffect(checked) {
        if (checked) {
            // Trigger 3: Particle confetti burst
            particleProgress.snapTo(0f)
            particleProgress.animateTo(1f, animationSpec = tween(durationMillis = 460, easing = FastOutSlowInEasing))
        } else {
            particleProgress.snapTo(0f)
        }
    }

    val particlePalette = remember {
        listOf(
            Color(0xFF6366F1), // Indigo
            Color(0xFF10B981), // Emerald
            Color(0xFFF59E0B), // Amber
            Color(0xFFEC4899), // Pink
            Color(0xFF06B6D4), // Cyan
            Color(0xFF8B5CF6)  // Purple
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size * 2f) // Extra canvas space for particle expansion
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onToggle() }
    ) {
        // Particle burst canvas
        if (particleProgress.value in 0.01f..0.99f) {
            val progress = particleProgress.value
            Canvas(modifier = Modifier.size(size * 2f)) {
                val centerOffset = Offset(this.size.width / 2f, this.size.height / 2f)
                val maxRadius = size.toPx() * 1.05f
                val currentRadius = progress * maxRadius
                val alpha = (1f - progress).coerceIn(0f, 1f)
                val particleCount = 10

                for (i in 0 until particleCount) {
                    val angle = (i.toFloat() / particleCount) * (2 * Math.PI) + (progress * 0.3)
                    val px = centerOffset.x + (currentRadius * cos(angle)).toFloat()
                    val py = centerOffset.y + (currentRadius * sin(angle)).toFloat()
                    val color = particlePalette[i % particlePalette.size].copy(alpha = alpha)
                    val pSize = (3.dp.toPx() * (1f - progress * 0.4f)).coerceAtLeast(1f)

                    drawCircle(
                        color = color,
                        radius = pSize,
                        center = Offset(px, py)
                    )
                }
            }
        }

        // Checkbox Surface
        Surface(
            shape = shape,
            color = if (checked) checkedColor else Color.Transparent,
            border = if (!checked) BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline) else null,
            modifier = Modifier
                .size(size)
                .scale(scaleAnim.value)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (checkmarkScale.value > 0.05f) {
                    Text(
                        text = "✓",
                        color = checkmarkColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.value * 0.52f).sp,
                        modifier = Modifier
                            .scale(checkmarkScale.value)
                            .rotate(checkmarkRotation.value)
                    )
                }
            }
        }
    }
}
