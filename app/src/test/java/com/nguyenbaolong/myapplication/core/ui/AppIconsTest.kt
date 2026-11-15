package com.nguyenbaolong.myapplication.core.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the central icon registry.
 *
 * All [AppIcons] vectors are built from SVG path data at object initialization.
 * A malformed or unsupported path throws at that point, so touching the object
 * is enough to ensure every icon used by the app can be created.
 */
class AppIconsTest {

    @Test
    fun allIconsInitializeWithValidVectorPaths() {
        val icons = listOf(
            AppIcons.Morning,
            AppIcons.Afternoon,
            AppIcons.Evening,
            AppIcons.Schedule,
            AppIcons.Habits,
            AppIcons.Journal,
            AppIcons.Reminder,
            AppIcons.Settings,
            AppIcons.Theme,
            AppIcons.WipeData,
            AppIcons.Warning,
            AppIcons.Privacy,
            AppIcons.Guide,
            AppIcons.Language,
            AppIcons.Streak,
            AppIcons.Goal,
            AppIcons.Insight,
            AppIcons.Trend,
            AppIcons.Done,
            AppIcons.Stats,
            AppIcons.Celebrate,
            AppIcons.Chat,
            AppIcons.SmartAI,
            AppIcons.AiBot,
            AppIcons.Meditation,
            AppIcons.Trophy,
            AppIcons.Fitness,
            AppIcons.Study,
            AppIcons.Work,
            AppIcons.Personal,
            AppIcons.Water,
            AppIcons.Strength,
            AppIcons.Other,
            AppIcons.Calendar,
            AppIcons.Notification,
            AppIcons.Action,
            AppIcons.SystemTheme,
            AppIcons.Bolt,
            AppIcons.Mic,
            AppIcons.Sparkle,
            AppIcons.PushPin,
            AppIcons.Block,
            AppIcons.Download
        )

        icons.forEach { icon ->
            assertEquals(24f, icon.defaultWidth.value, 0.01f)
            assertTrue(icon.root.size > 0)
        }
    }
}
