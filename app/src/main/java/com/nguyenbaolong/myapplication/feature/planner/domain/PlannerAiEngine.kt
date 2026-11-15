package com.nguyenbaolong.myapplication.feature.planner.domain

import com.nguyenbaolong.myapplication.core.utils.TimeOfDay
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityCategory
import java.util.UUID

data class ProposalItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: ActivityCategory,
    val isHabit: Boolean,
    val timeOfDay: TimeOfDay? = null,
    val frequencyType: String = "DAILY",
    val targetDays: String = "",
    val targetCompletionsPerWeek: Int = 7,
    val isSelected: Boolean = true
)

data class ProposalPlan(
    val goalSummary: String,
    val items: List<ProposalItem>
)

object PlannerAiEngine {

    fun generatePlan(prompt: String): ProposalPlan {
        val lower = prompt.lowercase()

        return when {
            lower.contains("run") || lower.contains("5k") || lower.contains("marathon") || lower.contains("jog") -> {
                ProposalPlan(
                    goalSummary = "Beginner Running & Cardio Schedule",
                    items = listOf(
                        ProposalItem(
                            title = "Morning warm-up & dynamic stretching",
                            category = ActivityCategory.FITNESS,
                            isHabit = false,
                            timeOfDay = TimeOfDay.MORNING
                        ),
                        ProposalItem(
                            title = "Jog 20-30 mins at comfortable pace",
                            category = ActivityCategory.FITNESS,
                            isHabit = true,
                            frequencyType = "SPECIFIC_DAYS",
                            targetDays = "1,3,5", // Mon, Wed, Fri
                            targetCompletionsPerWeek = 3
                        ),
                        ProposalItem(
                            title = "Hydrate with 2L water daily",
                            category = ActivityCategory.FITNESS,
                            isHabit = true,
                            frequencyType = "DAILY",
                            targetCompletionsPerWeek = 7
                        ),
                        ProposalItem(
                            title = "Post-run recovery walk & cool down",
                            category = ActivityCategory.FITNESS,
                            isHabit = false,
                            timeOfDay = TimeOfDay.MORNING
                        )
                    )
                )
            }
            lower.contains("study") || lower.contains("exam") || lower.contains("learn") || lower.contains("code") || lower.contains("kotlin") -> {
                ProposalPlan(
                    goalSummary = "Daily Focused Study & Skill Acquisition",
                    items = listOf(
                        ProposalItem(
                            title = "Review yesterday's notes & outline target",
                            category = ActivityCategory.STUDY,
                            isHabit = false,
                            timeOfDay = TimeOfDay.MORNING
                        ),
                        ProposalItem(
                            title = "Daily 30-min deep study session",
                            category = ActivityCategory.STUDY,
                            isHabit = true,
                            frequencyType = "DAILY",
                            targetCompletionsPerWeek = 7
                        ),
                        ProposalItem(
                            title = "Solve practice coding/homework exercises",
                            category = ActivityCategory.STUDY,
                            isHabit = false,
                            timeOfDay = TimeOfDay.AFTERNOON
                        ),
                        ProposalItem(
                            title = "Read 15 pages of technical book",
                            category = ActivityCategory.STUDY,
                            isHabit = true,
                            frequencyType = "DAILY",
                            targetCompletionsPerWeek = 7
                        )
                    )
                )
            }
            lower.contains("sleep") || lower.contains("meditat") || lower.contains("health") || lower.contains("mindful") || lower.contains("stress") -> {
                ProposalPlan(
                    goalSummary = "Mindfulness, Recovery & Sleep Habit",
                    items = listOf(
                        ProposalItem(
                            title = "10-minute mindfulness breathing",
                            category = ActivityCategory.PERSONAL,
                            isHabit = true,
                            frequencyType = "DAILY",
                            targetCompletionsPerWeek = 7
                        ),
                        ProposalItem(
                            title = "Drink warm water upon waking",
                            category = ActivityCategory.PERSONAL,
                            isHabit = true,
                            frequencyType = "DAILY",
                            targetCompletionsPerWeek = 7
                        ),
                        ProposalItem(
                            title = "Screen-free 30 minutes before bed",
                            category = ActivityCategory.PERSONAL,
                            isHabit = false,
                            timeOfDay = TimeOfDay.EVENING
                        ),
                        ProposalItem(
                            title = "Evening reflection & gratitudes",
                            category = ActivityCategory.PERSONAL,
                            isHabit = false,
                            timeOfDay = TimeOfDay.EVENING
                        )
                    )
                )
            }
            lower.contains("work") || lower.contains("project") || lower.contains("sprint") || lower.contains("deep work") -> {
                ProposalPlan(
                    goalSummary = "High-Impact Deep Work Sprint",
                    items = listOf(
                        ProposalItem(
                            title = "Daily standup & define top 3 priorities",
                            category = ActivityCategory.WORK,
                            isHabit = false,
                            timeOfDay = TimeOfDay.MORNING
                        ),
                        ProposalItem(
                            title = "90-minute uninterrupted deep work block",
                            category = ActivityCategory.WORK,
                            isHabit = true,
                            frequencyType = "SPECIFIC_DAYS",
                            targetDays = "1,2,3,4,5", // Weekdays
                            targetCompletionsPerWeek = 5
                        ),
                        ProposalItem(
                            title = "Clear inbox and review team PRs",
                            category = ActivityCategory.WORK,
                            isHabit = false,
                            timeOfDay = TimeOfDay.AFTERNOON
                        )
                    )
                )
            }
            else -> {
                val cleanedPrompt = prompt.trim().replaceFirstChar { it.uppercase() }
                ProposalPlan(
                    goalSummary = "Custom Plan: $cleanedPrompt",
                    items = listOf(
                        ProposalItem(
                            title = "Prepare and start $cleanedPrompt",
                            category = ActivityCategory.OTHER,
                            isHabit = false,
                            timeOfDay = TimeOfDay.MORNING
                        ),
                        ProposalItem(
                            title = "Practice $cleanedPrompt daily consistency",
                            category = ActivityCategory.OTHER,
                            isHabit = true,
                            frequencyType = "DAILY",
                            targetCompletionsPerWeek = 7
                        ),
                        ProposalItem(
                            title = "Review progress on $cleanedPrompt",
                            category = ActivityCategory.OTHER,
                            isHabit = false,
                            timeOfDay = TimeOfDay.EVENING
                        )
                    )
                )
            }
        }
    }
}
