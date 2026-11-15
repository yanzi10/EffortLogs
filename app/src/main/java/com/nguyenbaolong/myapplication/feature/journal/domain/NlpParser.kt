package com.nguyenbaolong.myapplication.feature.journal.domain

import com.nguyenbaolong.myapplication.core.utils.TimeOfDay
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityCategory
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

data class NlpResult(
    val rawInput: String,
    val detectedAction: String,
    val category: ActivityCategory,
    val durationMinutes: Int?,
    val activityDate: LocalDate,
    val timeOfDay: TimeOfDay?
)

class NlpParser(
    private val clock: () -> LocalDate = { LocalDate.now() }
) {

    private val numberWordsMap = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
        "ten" to 10,
        "fifteen" to 15,
        "twenty" to 20,
        "twenty-five" to 25,
        "twenty five" to 25,
        "thirty" to 30,
        "forty" to 40,
        "forty-five" to 45,
        "forty five" to 45,
        "fifty" to 50
    )

    fun parse(input: String, baseDate: LocalDate = clock()): NlpResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return NlpResult(
                rawInput = "",
                detectedAction = "Activity",
                category = ActivityCategory.OTHER,
                durationMinutes = null,
                activityDate = baseDate,
                timeOfDay = null
            )
        }

        val (date, timeOfDay, dateRegexMatches) = extractDateAndTimeOfDay(trimmed, baseDate)
        val (duration, durationRegexMatches) = extractDuration(trimmed)
        val category = KeywordDictionary.detectCategory(trimmed)

        // Clean action by removing parsed date/time & duration phrases
        var action = trimmed
        for (pattern in dateRegexMatches + durationRegexMatches) {
            action = action.replace(pattern, " ")
        }
        // Remove trailing or leading prepositions like "for", "at", "in"
        action = action.replace(Regex("""\b(for|in|at|during)\s*$""", RegexOption.IGNORE_CASE), "")
        action = action.replace(Regex("""^\s*(for|in|at)\s*""", RegexOption.IGNORE_CASE), "")
        action = action.replace(Regex("""\s+"""), " ").trim()

        if (action.isBlank()) {
            action = trimmed
        } else {
            action = action.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        }

        return NlpResult(
            rawInput = trimmed,
            detectedAction = action,
            category = category,
            durationMinutes = duration,
            activityDate = date,
            timeOfDay = timeOfDay
        )
    }

    private fun extractDateAndTimeOfDay(
        text: String,
        baseDate: LocalDate
    ): Triple<LocalDate, TimeOfDay?, List<String>> {
        val lower = text.lowercase()
        val matchedPhrases = mutableListOf<String>()
        var date = baseDate
        var timeOfDay: TimeOfDay? = null

        if (Regex("""\byesterday\b""").containsMatchIn(lower)) {
            date = baseDate.minusDays(1)
            matchedPhrases.add("yesterday")
        }

        if (Regex("""\b(this morning|in the morning|morning)\b""").containsMatchIn(lower)) {
            timeOfDay = TimeOfDay.MORNING
            matchedPhrases.add("this morning")
            matchedPhrases.add("in the morning")
        } else if (Regex("""\b(this afternoon|in the afternoon|afternoon)\b""").containsMatchIn(lower)) {
            timeOfDay = TimeOfDay.AFTERNOON
            matchedPhrases.add("this afternoon")
            matchedPhrases.add("in the afternoon")
        } else if (Regex("""\b(this evening|in the evening|evening|tonight)\b""").containsMatchIn(lower)) {
            timeOfDay = TimeOfDay.EVENING
            matchedPhrases.add("this evening")
            matchedPhrases.add("in the evening")
            matchedPhrases.add("tonight")
        }

        return Triple(date, timeOfDay, matchedPhrases)
    }

    private fun extractDuration(text: String): Pair<Int?, List<String>> {
        val lower = text.lowercase()
        val matchedPhrases = mutableListOf<String>()

        // 1. "half an hour" / "half hour"
        val halfHourRegex = Regex("""\b(for\s+)?(half\s+an?\s+hour|half\s+hour)\b""", RegexOption.IGNORE_CASE)
        val halfMatch = halfHourRegex.find(lower)
        if (halfMatch != null) {
            matchedPhrases.add(halfMatch.value)
            return Pair(30, matchedPhrases)
        }

        // 2. "an hour" / "one hour"
        val anHourRegex = Regex("""\b(for\s+)?(an?\s+hour)\b""", RegexOption.IGNORE_CASE)
        val anHourMatch = anHourRegex.find(lower)
        if (anHourMatch != null) {
            matchedPhrases.add(anHourMatch.value)
            return Pair(60, matchedPhrases)
        }

        // 3. Combined: "1 hour 30 minutes", "2 hours and 15 mins", "1 hr 45 min"
        val combinedRegex = Regex(
            """\b(?:for\s+)?(\d+)\s*(?:hours?|hrs?|h)\s*(?:and\s*)?(\d+)\s*(?:minutes?|mins?|m)\b""",
            RegexOption.IGNORE_CASE
        )
        val combinedMatch = combinedRegex.find(lower)
        if (combinedMatch != null) {
            matchedPhrases.add(combinedMatch.value)
            val hours = combinedMatch.groupValues[1].toIntOrNull() ?: 0
            val minutes = combinedMatch.groupValues[2].toIntOrNull() ?: 0
            val total = hours * 60 + minutes
            if (total in 1..1440) return Pair(total, matchedPhrases)
        }

        // 4. Decimal or integer hours: "1.5 hours", "2 hours", "3 hrs"
        val hoursRegex = Regex("""\b(?:for\s+)?(\d+(?:\.\d+)?)\s*(?:hours?|hrs?|h)\b""", RegexOption.IGNORE_CASE)
        val hoursMatch = hoursRegex.find(lower)
        if (hoursMatch != null) {
            val hoursVal = hoursMatch.groupValues[1].toDoubleOrNull()
            if (hoursVal != null && hoursVal > 0) {
                matchedPhrases.add(hoursMatch.value)
                val totalMinutes = (hoursVal * 60).toInt()
                if (totalMinutes in 1..1440) return Pair(totalMinutes, matchedPhrases)
            }
        }

        // 5. Minutes: "30 minutes", "45 mins", "15 min"
        val minutesRegex = Regex("""\b(?:for\s+)?(\d+)\s*(?:minutes?|mins?|m)\b""", RegexOption.IGNORE_CASE)
        val minutesMatch = minutesRegex.find(lower)
        if (minutesMatch != null) {
            val minutesVal = minutesMatch.groupValues[1].toIntOrNull()
            if (minutesVal != null && minutesVal in 1..1440) {
                matchedPhrases.add(minutesMatch.value)
                return Pair(minutesVal, matchedPhrases)
            }
        }

        // 6. Number words: "thirty minutes", "two hours", "fifteen minutes", "one hour"
        for ((word, num) in numberWordsMap) {
            val wordMinutesRegex = Regex("""\b(?:for\s+)?$word\s*(?:minutes?|mins?)\b""", RegexOption.IGNORE_CASE)
            val matchMin = wordMinutesRegex.find(lower)
            if (matchMin != null) {
                matchedPhrases.add(matchMin.value)
                return Pair(num, matchedPhrases)
            }

            val wordHoursRegex = Regex("""\b(?:for\s+)?$word\s*(?:hours?|hrs?)\b""", RegexOption.IGNORE_CASE)
            val matchHr = wordHoursRegex.find(lower)
            if (matchHr != null) {
                matchedPhrases.add(matchHr.value)
                val total = num * 60
                if (total in 1..1440) return Pair(total, matchedPhrases)
            }
        }

        return Pair(null, emptyList())
    }
}

