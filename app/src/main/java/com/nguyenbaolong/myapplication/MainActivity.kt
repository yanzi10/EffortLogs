package com.nguyenbaolong.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nguyenbaolong.myapplication.navigation.AppNavigation
import com.nguyenbaolong.myapplication.ui.theme.EffortLogsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as EffortLogApplication).container
        setContent {
            EffortLogsTheme {
                AppNavigation(container = container)
            }
        }
    }
}
