package com.nguyenbaolong.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nguyenbaolong.myapplication.feature.reminder.ReminderScheduler
import com.nguyenbaolong.myapplication.feature.settings.AppThemeMode
import com.nguyenbaolong.myapplication.feature.widget.EffortLogWidgetProvider
import com.nguyenbaolong.myapplication.navigation.AppNavigation
import com.nguyenbaolong.myapplication.ui.theme.EffortLogsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as EffortLogApplication).container

        // Schedule active daily reminders safely without blocking or crashing Activity
        try {
            ReminderScheduler.scheduleAll(this, container.settingsManager)
        } catch (_: Exception) {}

        val startWithVoice = intent?.getBooleanExtra(EffortLogWidgetProvider.EXTRA_START_VOICE, false) ?: false

        setContent {
            val settings by container.settingsManager.settings.collectAsStateWithLifecycle()
            val isDark = when (settings.themeMode) {
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            EffortLogsTheme(darkTheme = isDark) {
                AppNavigation(
                    container = container,
                    startWithVoice = startWithVoice
                )
            }
        }
    }
}
