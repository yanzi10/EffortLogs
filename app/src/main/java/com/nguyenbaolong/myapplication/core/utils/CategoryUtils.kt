package com.nguyenbaolong.myapplication.core.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.nguyenbaolong.myapplication.core.ui.AppIcons
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityCategory
import com.nguyenbaolong.myapplication.ui.theme.*

fun getCategoryIcon(category: ActivityCategory): ImageVector = when (category) {
    ActivityCategory.STUDY -> AppIcons.Study
    ActivityCategory.FITNESS -> AppIcons.Fitness
    ActivityCategory.WORK -> AppIcons.Work
    ActivityCategory.PERSONAL -> AppIcons.Personal
    ActivityCategory.OTHER -> AppIcons.Other
}

fun getCategoryEmoji(category: ActivityCategory): String = when (category) {
    ActivityCategory.STUDY -> "📖"
    ActivityCategory.FITNESS -> "🏃"
    ActivityCategory.WORK -> "💼"
    ActivityCategory.PERSONAL -> "🌿"
    ActivityCategory.OTHER -> "🎯"
}

fun getCategoryName(category: ActivityCategory): String = when (category) {
    ActivityCategory.STUDY -> "Study"
    ActivityCategory.FITNESS -> "Fitness & Health"
    ActivityCategory.WORK -> "Work"
    ActivityCategory.PERSONAL -> "Personal"
    ActivityCategory.OTHER -> "Other"
}

fun getCategoryColor(category: ActivityCategory): Color = when (category) {
    ActivityCategory.STUDY -> CategoryStudy
    ActivityCategory.FITNESS -> CategoryFitness
    ActivityCategory.WORK -> CategoryWork
    ActivityCategory.PERSONAL -> CategoryPersonal
    ActivityCategory.OTHER -> CategoryOther
}

fun getCategoryDarkBg(category: ActivityCategory): Color = when (category) {
    ActivityCategory.STUDY -> CategoryStudyBg
    ActivityCategory.FITNESS -> CategoryFitnessBg
    ActivityCategory.WORK -> CategoryWorkBg
    ActivityCategory.PERSONAL -> CategoryPersonalBg
    ActivityCategory.OTHER -> CategoryOtherBg
}
