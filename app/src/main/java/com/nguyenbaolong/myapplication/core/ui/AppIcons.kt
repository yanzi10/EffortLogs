package com.nguyenbaolong.myapplication.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * Centralised Material Icon definitions.
 *
 * All app-wide icon references come from here so the application always uses
 * tinted Material icons rather than system-rendered emoji glyphs.
 * This guarantees consistent appearance across all Android OS versions
 * without relying on device-specific emoji fonts or extra dependencies.
 */
object AppIcons {

    private fun vector(name: String, pathData: String): ImageVector {
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = PathParser().parsePathString(pathData).toNodes(),
            fill = SolidColor(Color.White)
        ).build()
    }

    // ── Time-of-day ────────────────────────────────────────────────────────
    /** Represents the morning time slot (sunrise / twilight). */
    val Morning: ImageVector = vector(
        name = "Morning",
        pathData = "M20,15.31L23.31,12L20,8.69V4h-4.69L12,0.69L8.69,4H4v4.69L0.69,12L4,15.31V20h4.69L12,23.31L15.31,20H20V15.31zM12,18c-3.31,0-6-2.69-6-6s2.69-6 6-6s6,2.69 6,6S15.31,18 12,18z"
    )

    /** Represents the afternoon time slot (sun / light mode). */
    val Afternoon: ImageVector = vector(
        name = "Afternoon",
        pathData = "M12,7c-2.76,0-5,2.24-5,5s2.24,5 5,5 5-2.24 5-5-2.24-5-5-5zM2,13h2c.55,0 1-.45 1-1s-.45-1-1-1H2c-.55,0-1 .45-1,1s.45,1 1,1zm18,0h2c.55,0 1-.45 1-1s-.45-1-1-1h-2c-.55,0-1 .45-1,1s.45,1 1,1zM11,2v2c0 .55.45 1 1 1s1-.45 1-1V2c0-.55-.45-1-1-1s-1 .45-1,1zm0,18v2c0 .55.45 1 1 1s1-.45 1-1v-2c0-.55-.45-1-1-1s-1 .45-1,1zM5.99,4.58c-.39-.39-1.03-.39-1.41,0s-.39,1.03 0,1.41l1.06,1.06c.39.39 1.03.39 1.41,0s.39-1.03 0-1.41L5.99,4.58zm12.37,12.37c-.39-.39-1.03-.39-1.41,0s-.39,1.03 0,1.41l1.06,1.06c.39.39 1.03.39 1.41,0s.39-1.03 0-1.41l-1.06-1.06zm1.06-10.96c.39-.39.39-1.03 0-1.41s-1.03-.39-1.41,0l-1.06,1.06c-.39.39-.39,1.03 0,1.41s1.03.39 1.41,0l1.06-1.06zM7.05,18.36c.39-.39.39-1.03 0-1.41s-1.03-.39-1.41,0l-1.06,1.06c-.39.39-.39,1.03 0,1.41s1.03.39 1.41,0l1.06-1.06z"
    )

    /** Represents the evening / night time slot (crescent moon). */
    val Evening: ImageVector = vector(
        name = "Evening",
        pathData = "M12.3,2a10,10 0 0,0 -1.9,0.18 10,10 0 1,0 11.42,11.42A10,10 0 0,1 12.3,2Z"
    )

    // ── Navigation / Bento sections ────────────────────────────────────────
    /** Daily schedule / task list section header. */
    val Schedule: ImageVector = vector(
        name = "Schedule",
        pathData = "M19,3h-4.18C14.4,1.84 13.3,1 12,1c-1.3,0-2.4,0.84-2.82,2H5C3.9,3 3,3.9 3,5v14c0,1.1 0.9,2 2,2h14c1.1,0 2,-0.9 2,-2V5C21,3.9 20.1,3 19,3zM12,3c0.55,0 1,0.45 1,1s-0.45,1-1,1 -1,-0.45-1,-1 0.45,-1 1,-1zM14,15H7v-2h7V15zM17,11H7V9h10V11z"
    )

    /** Core habits / recurring section header. */
    val Habits: ImageVector = Icons.Default.Refresh

    /** Effort journal / reading section header. */
    val Journal: ImageVector = vector(
        name = "Journal",
        pathData = "M21,5c-1.11-.35-2.33-.5-3.5-.5-1.95,0-4.05.4-5.5,1.5-1.45-1.1-3.55-1.5-5.5-1.5S2.45,4.9 1,6v14.65c0,.25.25.5.5.5.1,0 .15-.05.25-.05C3.1,20.45 5.05,20 6.5,20c1.95,0 4.05.4 5.5,1.5 1.35-.85 3.8-1.5 5.5-1.5 1.65,0 3.35.3 4.75,1.05.1.05.15.05.25.05.25,0 .5-.25.5-.5V6c-.6-.45-1.25-.75-2-1zm0,13.5c-1.1-.35-2.3-.5-3.5-.5-1.7,0-4.15.65-5.5,1.5V8c1.35-.85 3.8-1.5 5.5-1.5 1.2,0 2.4.15 3.5.5v11.5z"
    )

    // ── Settings / Profile ─────────────────────────────────────────────────
    /** Start-of-day / alarm / reminder icon (clock / schedule). */
    val Reminder: ImageVector = vector(
        name = "Reminder",
        pathData = "M11.99,2C6.47,2 2,6.48 2,12s4.47,10 9.99,10C17.52,22 22,17.52 22,12S17.52,2 11.99,2zM12,20c-4.42,0-8-3.58-8-8s3.58-8 8-8 8,3.58 8,8-3.58,8-8,8zm.5-13H11v6l5.25,3.15.75-1.23-4.5-2.67z"
    )

    /** Settings / configuration icon. */
    val Settings: ImageVector = vector(
        name = "Settings",
        pathData = "M19.14,12.94c0.04,-0.3 0.06,-0.61 0.06,-0.94 0,-0.32 -0.02,-0.64 -0.07,-0.94l2.03,-1.58c0.18,-0.14 0.23,-0.41 0.12,-0.61l-1.92,-3.32c-0.12,-0.22 -0.37,-0.29 -0.59,-0.22l-2.39,0.96c-0.5,-0.38 -1.03,-0.7 -1.62,-0.94L14.4,2.81c-0.04,-0.24 -0.24,-0.41 -0.48,-0.41h-3.84c-0.24,0 -0.43,0.17 -0.47,0.41L9.25,5.35C8.66,5.59 8.12,5.92 7.63,6.29L5.24,5.33c-0.22,-0.08 -0.47,0 -0.59,0.22L2.74,8.87c-0.12,0.21 -0.08,0.47 0.12,0.61l2.03,1.58c-0.05,0.3 -0.09,0.63 -0.09,0.94s0.02,0.64 0.07,0.94l-2.03,1.58c-0.18,0.14 -0.23,0.41 -0.12,0.61l1.92,3.32c0.12,0.22 0.37,0.29 0.59,0.22l2.39,-0.96c0.5,0.38 1.03,0.7 1.62,0.94l0.36,2.54c0.05,0.24 0.24,0.41 0.48,0.41h3.84c0.24,0 0.44,-0.17 0.47,-0.41l0.36,-2.54c0.59,-0.24 1.13,-0.56 1.62,-0.94l2.39,0.96c0.22,0.08 0.47,0 0.59,-0.22l1.92,-3.32c0.12,-0.22 0.07,-0.47 -0.12,-0.61L19.14,12.94zM12,15.6c-1.98,0 -3.6,-1.62 -3.6,-3.6s1.62,-3.6 3.6,-3.6 3.6,1.62 3.6,3.6 -1.62,3.6 -3.6,3.6z"
    )

    /** Display theme / palette icon. */
    val Theme: ImageVector = vector(
        name = "Theme",
        pathData = "M12,3c-4.97,0-9,4.03-9,9 0,2.12.74,4.07 1.97,5.61L4.35,18.23c-.39.39-.39,1.02 0,1.41.39.39 1.02.39 1.41,0l1.9-1.9C9.09,18.51 10.49,19 12,19c4.97,0 9-4.03 9-9s-4.03-9-9-9zm-5.5,9c-.83,0-1.5-.67-1.5-1.5S5.67,9 6.5,9 8,9.67 8,10.5 7.33,12 6.5,12zm3-4C8.67,8 8,7.33 8,6.5S8.67,5 9.5,5s1.5.67 1.5,1.5S10.33,8 9.5,8zm5,0c-.83,0-1.5-.67-1.5-1.5S13.67,5 14.5,5s1.5.67 1.5,1.5S15.33,8 14.5,8zm3,4c-.83,0-1.5-.67-1.5-1.5S16.67,9 17.5,9s1.5.67 1.5,1.5-.67,1.5-1.5,1.5z"
    )

    /** Wipe / delete data icon. */
    val WipeData: ImageVector = Icons.Default.Delete

    /** Warning / caution icon. */
    val Warning: ImageVector = Icons.Default.Warning

    /** Privacy / lock / security icon. */
    val Privacy: ImageVector = Icons.Default.Lock

    /** Tutorial / onboarding guide replay icon. */
    val Guide: ImageVector = Journal

    /** Language / speech recognition icon. */
    val Language: ImageVector = vector(
        name = "Language",
        pathData = "M11.99,2C6.47,2 2,6.48 2,12s4.47,10 9.99,10C17.52,22 22,17.52 22,12S17.52,2 11.99,2zm6.93,6h-2.95c-.32-1.25-.78-2.45-1.38-3.56 1.84.63 3.37,1.91 4.33,3.56zM12,4.04c.83,1.2 1.48,2.53 1.91,3.96h-3.82c.43-1.43 1.08-2.76 1.91-3.96zM4.26,14C4.1,13.36 4,12.69 4,12s.1-1.36.26-2h3.38c-.08.66-.14,1.32-.14,2 0,.68.06,1.34.14,2H4.26zm.82,2h2.95c.32,1.25.78,2.45 1.38,3.56-1.84-.63-3.37-1.9-4.33-3.56zm2.95-8H5.08c.96-1.66 2.49-2.93 4.33-3.56C8.81,5.55 8.35,6.75 8.03,8zM12,19.96c-.83-1.2-1.48-2.53-1.91-3.96h3.82c-.43,1.43-1.08,2.76-1.91,3.96zM14.34,14H9.66c-.09-.66-.16-1.32-.16-2 0-.68.07-1.35.16-2h4.68c.09.65.16,1.32.16,2 0,.68-.07,1.34-.16,2zm.25,5.56c.6-1.11 1.06-2.31 1.38-3.56h2.95c-.96,1.65-2.49,2.93-4.33,3.56zM16.36,14c.08-.66.14-1.32.14-2 0-.68-.06-1.34-.14-2h3.38c.16.64.26,1.31.26,2s-.1,1.36-.26,2h-3.38z"
    )

    // ── Stats / Dashboard ──────────────────────────────────────────────────
    /** Streak / fire icon. */
    val Streak: ImageVector = vector(
        name = "Streak",
        pathData = "M13.5,0.67s.74,2.65 .74,4.8c0,2.06-1.35,3.73-3.41,3.73-2.07,0-3.63-1.67-3.63-3.73l.03-.36C5.21,7.51 4,10.61 4,14c0,4.42 3.58,8 8,8s8-3.58 8-8C20,8.61 17.41,3.8 13.5,0.67zM11.71,19c-1.78,0-3.22-1.4-3.22-3.14 0-1.62 1.05-2.76 2.81-3.12 1.77-.36 3.6-1.21 4.62-2.58.39,1.29.59,2.65.59,4.04 0,2.65-2.15,4.8-4.8,4.8z"
    )

    /** Target / goal / trophy icon. */
    val Goal: ImageVector = Icons.Default.Star

    /** Insight / tip icon. */
    val Insight: ImageVector = Icons.Default.Info

    /** Trend / growth chart icon. */
    val Trend: ImageVector = vector(
        name = "Trend",
        pathData = "M16,6l2.29,2.29-4.88,4.88-4-4L2,16.59 3.41,18l6-6 4,4 6.3-6.29L22,12V6z"
    )

    /** Completed / checked icon. */
    val Done: ImageVector = Icons.Default.Done

    /** Data / usage icon. */
    val Stats: ImageVector = Icons.Default.List

    // ── Companion / onboarding ─────────────────────────────────────────────
    /** Celebration / level-up icon. */
    val Celebrate: ImageVector = Icons.Default.Star

    /** Chat / companion message icon. */
    val Chat: ImageVector = vector(
        name = "Chat",
        pathData = "M20,2H4c-1.1,0-2,.9-2,2v18l4-4h14c1.1,0 2-.9 2-2V4c0-1.1-.9-2-2-2z"
    )

    /** Brain / smart AI processing icon. */
    val SmartAI: ImageVector = vector(
        name = "SmartAI",
        pathData = "M12,2a2,2 0 0,1 2,2c0,.74-.4,1.38-1,1.72V7h4a3,3 0 0,1 3,3v8a3,3 0 0,1-3,3H7a3,3 0 0,1-3-3v-8a3,3 0 0,1 3-3h4V5.72c-.6-.34-1-.98-1-1.72a2,2 0 0,1 2-2M7.5,12a1.5,1.5 0 0,0 0,3 1.5,1.5 0 0,0 0-3m9,0a1.5,1.5 0 0,0 0,3 1.5,1.5 0 0,0 0-3M16,17H8v1h8z"
    )

    /** Smart AI bot icon. */
    val AiBot: ImageVector = SmartAI

    /** Mindfulness / meditation icon (companion growth stage). */
    val Meditation: ImageVector = vector(
        name = "Meditation",
        pathData = "M12,4a2,2 0 1,0 0,4a2,2 0 1,0 0,-4zM21,16v-2c-2.24,0-4.16-0.96-5.6-2.68l-1.34-1.6C13.68,9.26,13.12,9,12.53,9h-1.05c-0.59,0-1.15,0.26-1.53,0.72l-1.34,1.6C7.16,13.04,5.24,14,3,14v2c2.77,0,5.19-1.17,7-3.25V15l-3.88,1.55C5.45,16.82,5,17.48,5,18.21C5,19.2,5.8,20,6.79,20H9v-0.5c0-1.38,1.12-2.5,2.5-2.5h3c0.28,0,0.5,0.22,0.5,0.5S14.78,18,14.5,18h-3c-0.83,0-1.5,0.67-1.5,1.5V20h7.21C18.2,20,19,19.2,19,18.21c0-0.73-0.45-1.39-1.12-1.66L14,15v-2.25C15.81,14.83,18.23,16,21,16z"
    )

    /** Achievement / trophy icon. */
    val Trophy: ImageVector = vector(
        name = "Trophy",
        pathData = "M19,5h-2V3H7v2H5C3.9,5,3,5.9,3,7v1c0,2.55,1.92,4.63,4.39,4.94c0.63,1.5,1.98,2.63,3.61,2.96V19H7v2h10v-2h-4v-3.1c1.63-0.33,2.98-1.46,3.61-2.96C19.08,12.63,21,10.55,21,8V7C21,5.9,20.1,5,19,5zM5,8V7h2v3.82C5.84,10.4,5,9.3,5,8zM19,8c0,1.3-0.84,2.4-2,2.82V7h2V8z"
    )

    // ── Category (Habit / Journal) ─────────────────────────────────────────
    /** Fitness / running category icon. */
    val Fitness: ImageVector = vector(
        name = "Fitness",
        pathData = "M13.49,5.48c1.1,0 2-.9 2-2s-.9-2-2-2-2,.9-2,2 .9,2 2,2zm-3.6,13.9l1-4.4 2.1,2v6h2v-7.5l-2.1-2 .6-3c1.3,1.5 3.3,2.5 5.5,2.5v-2c-1.9,0-3.5-1-4.3-2.4l-1-1.6c-.4-.6-1-1-1.7-1-.3,0-.5.1-.8.1l-5.2,2.2v4.7h2v-3.4l1.8-.7-1.6,8.1-4.9-1-.4,2 7,1.4z"
    )

    /** Study / reading category icon. */
    val Study: ImageVector = Journal

    /** Work / professional category icon. */
    val Work: ImageVector = vector(
        name = "Work",
        pathData = "M20,6h-4V4c0-1.11-.89-2-2-2h-4c-1.11,0-2,.89-2,2v2H4c-1.11,0-1.99.89-1.99,2L2,19c0,1.11.89,2 2,2h16c1.11,0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6,0h-4V4h4v2z"
    )

    /** Meditation / personal category icon. */
    val Personal: ImageVector = Icons.Default.Favorite

    /** Water / hydration category icon. */
    val Water: ImageVector = vector(
        name = "Water",
        pathData = "M12,2c-5.33,4.55-8,8.48-8,11.8 0,4.98 3.8,8.2 8,8.2s8-3.22 8-8.2c0-3.32-2.67-7.25-8-11.8z"
    )

    /** Strength / gym category icon. */
    val Strength: ImageVector = Fitness

    /** Other / general category icon. */
    val Other: ImageVector = Icons.Default.Star

    // ── Calendar & Actions ───────────────────────────────────────────────────
    /** Calendar / date icon. */
    val Calendar: ImageVector = Icons.Default.DateRange

    /** Notification icon. */
    val Notification: ImageVector = Icons.Default.Notifications

    /** Edit / action icon. */
    val Action: ImageVector = Icons.Default.Edit

    /** Auto / system theme mode. */
    val SystemTheme: ImageVector = vector(
        name = "SystemTheme",
        pathData = "M12,4V1L8,5l4,4V6c3.31,0 6,2.69 6,6 0,1.01-.25,1.97-.7,2.8l1.46,1.46C19.54,15.03 20,13.57 20,12c0-4.42-3.58-8-8-8zm0,14c-3.31,0-6-2.69-6-6 0-1.01.25-1.97.7-2.8L5.24,7.74C4.46,8.97 4,10.43 4,12c0,4.42 3.58,8 8,8v3l4-4-4-4v3z"
    )

    /** Lightning bolt / quick action icon. */
    val Bolt: ImageVector = vector(
        name = "Bolt",
        pathData = "M7,2v11h3v9l7,-12h-4l4,-8z"
    )

    /** Microphone icon. */
    val Mic: ImageVector = vector(
        name = "Mic",
        pathData = "M12,14c1.66,0 3,-1.34 3,-3V5c0,-1.66 -1.34,-3 -3,-3S9,3.34 9,5v6c0,1.66 1.34,3 3,3zM17.3,11c0,3 -2.54,5.1 -5.3,5.1S6.7,14 6.7,11H5c0,3.41 2.72,6.23 6,6.72V21h2v-3.28c3.28,-0.48 6,-3.3 6,-6.72h-1.7z"
    )

    /** Sparkles / AI generation icon. */
    val Sparkle: ImageVector = vector(
        name = "Sparkle",
        pathData = "M19,9l1.25,-2.75L23,5l-2.75,-1.25L19,1l-1.25,2.75L15,5l2.75,1.25zM9,11l2.5,-5.5L14,11l5.5,2.5L14,16l-2.5,5.5L9,16l-5.5,-2.5zM19,15l-1.25,2.75L15,19l2.75,1.25L19,23l1.25,-2.75L23,19l-2.75,-1.25z"
    )

    /** Pin / marker icon. */
    val PushPin: ImageVector = vector(
        name = "PushPin",
        pathData = "M16,9V4l1,0V2H7v2l1,0v5c0,1.66-1.34,3-3,3v2h5.97v7l1,1 1-1v-7H19v-2c-1.66,0-3-1.34-3-3z"
    )

    /** Block / forbidden icon. */
    val Block: ImageVector = vector(
        name = "Block",
        pathData = "M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10-4.48 10-10S17.52,2 12,2zM4,12c0-4.42 3.58-8 8-8 1.85,0 3.55.63 4.9,1.69L5.69,16.9C4.63,15.55 4,13.85 4,12zm8,8c-1.85,0-3.55-.63-4.9-1.69L18.31,7.1C19.37,8.45 20,10.15 20,12c0,4.42-3.58,8-8,8z"
    )

    /** Download / export icon. */
    val Download: ImageVector = vector(
        name = "Download",
        pathData = "M19,9h-4V3H9v6H5l7,7 7-7zM5,18v2h14v-2H5z"
    )
}
