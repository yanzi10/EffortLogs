package com.nguyenbaolong.myapplication.feature.journal.domain

import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityCategory

object KeywordDictionary {

    val categoryKeywords: Map<ActivityCategory, List<String>> = mapOf(
        ActivityCategory.FITNESS to listOf(
            "gym", "workout", "work out", "exercise", "running", "run", "jogging", "jog",
            "swimming", "swim", "cycling", "cycle", "bike", "biking", "yoga", "pilates",
            "cardio", "pushup", "pushups", "pullup", "pullups", "fitness", "walk", "walking",
            "hike", "hiking", "lifting", "weights", "football", "soccer", "basketball",
            "badminton", "tennis", "stretching", "stretch"
        ),
        ActivityCategory.STUDY to listOf(
            "study", "studying", "read", "reading", "homework", "exam", "revision", "revise",
            "math", "mathematics", "physics", "chemistry", "biology", "history", "english",
            "lecture", "class", "course", "learn", "learning", "assignment", "tutorial",
            "code", "coding", "programming", "algorithm", "research", "paper", "quiz",
            "book", "library"
        ),
        ActivityCategory.WORK to listOf(
            "meeting", "sync", "standup", "client", "presentation", "report", "email",
            "emails", "project", "deploy", "deployment", "bug", "debugging", "feature",
            "office", "interview", "review", "pr", "pull request", "document", "documentation",
            "strategy", "planning", "call with team", "work", "working", "customer", "sales"
        ),
        ActivityCategory.PERSONAL to listOf(
            "meditate", "meditation", "cook", "cooking", "meal", "breakfast", "lunch", "dinner",
            "grocery", "shopping", "clean", "cleaning", "laundry", "dishes", "family",
            "call mom", "call dad", "nap", "sleep", "movie", "gaming", "game", "relax",
            "journal", "journaling", "skincare", "doctor", "dentist", "music", "guitar", "piano"
        )
    )

    fun detectCategory(text: String): ActivityCategory {
        val lower = text.lowercase()
        var bestCategory = ActivityCategory.OTHER
        var maxMatches = 0

        for ((category, keywords) in categoryKeywords) {
            val count = keywords.count { kw ->
                val regex = Regex("""\b${Regex.escape(kw)}\b""", RegexOption.IGNORE_CASE)
                regex.containsMatchIn(lower)
            }
            if (count > maxMatches) {
                maxMatches = count
                bestCategory = category
            }
        }
        return bestCategory
    }
}

