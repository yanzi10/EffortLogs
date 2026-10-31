# Technical Requirements and Implementation Rules

**Status:** Temporary MVP baseline for a six-week, five-member student project. Implement only the requirements approved by the team and keep the scope small enough to verify.

## 1. Voice Input (Speech-to-Text)

- **Platform API:** Use Android's built-in `android.speech.SpeechRecognizer`; do not add an external speech SDK without approval.
- **Initial language:** English (`en-US`).
- **Preferred mode:** On Android API 31 or later, check `SpeechRecognizer.isOnDeviceRecognitionAvailable(context)` before creating an on-device recognizer with `SpeechRecognizer.createOnDeviceSpeechRecognizer(context)`.
- **Privacy-aware fallback:** Do not silently switch to the standard system recognizer when on-device recognition is unavailable. The standard recognizer may use remote servers. Instead, offer manual text input and, if the team implements it, let the user explicitly choose the standard system recognizer after explaining that audio processing may be performed by the selected system service and may require a network connection.
- **API 26–30:** Do not claim that speech recognition is on-device or offline. Offer manual input by default; only use the standard system recognizer after the user has made the informed choice described above.
- **Permission and lifecycle:** Request `RECORD_AUDIO` at runtime when needed. Handle permission denial, cancellation, no speech, unavailable services, and recognition errors gracefully. Call `SpeechRecognizer` methods on the main thread, set its listener before issuing recognition commands, and call `destroy()` when it is no longer needed.
- **Manifest visibility:** Check the Android documentation for the required speech-recognition service `<queries>` declaration for the app's target SDK and add it only as part of an approved voice-input task.
- **Data handling:** Do not record or persist raw audio. Do not log transcripts or audio-related personal content to Logcat. Only pass recognized text into the NLP and confirmation flow.
- **Fallback:** If recognition is unavailable or fails, keep the user in control and offer editable manual text input instead of crashing or silently losing the entry.

## 2. Rule-Based Natural Language Processing (NLP)

The first release uses a small local Kotlin parser. Do not add ML/NLP libraries unless a concrete requirement cannot reasonably be met with the existing stack and the user approves the dependency.

### 2.1 Pipeline

1. Receive editable text from voice recognition or manual entry.
2. Normalize whitespace and case for matching while preserving the original text for the confirmation screen and, after confirmation, the activity record.
3. Extract a duration if a supported pattern is found.
4. Match action keywords and nouns against a centralized `KeywordDictionary` to suggest an action and category.
5. Extract only the date/time-of-day information covered by the supported rules below.
6. Show all extracted and defaulted values on a confirmation screen. Allow the user to edit them before saving.

### 2.2 Duration Extraction

- Support common English numeric forms such as `30 minutes`, `30 mins`, `2 hours`, `1.5 hours`, `half an hour`, and `half hour`.
- Support common English number-word forms such as `thirty minutes` and `one hour`.
- Convert successfully parsed durations to integer minutes (for example, `2 hours` becomes `120`).
- Reject negative, zero, malformed, or unreasonable values according to a simple documented validation limit. Do not silently coerce invalid input into a plausible duration.
- If no duration is found, leave it unset; do not guess. The user may leave it blank or enter a value on the confirmation screen.
- Cover supported forms and edge cases with JUnit 4 unit tests.

### 2.3 Action and Category Classification

- The fixed MVP category set is `STUDY`, `FITNESS`, `WORK`, `PERSONAL`, and `OTHER`.
- Keep a local, centralized `KeywordDictionary` rather than scattering keyword conditions across screens.
- Example mappings: `study`, `read`, or `homework` can suggest `STUDY`; `gym`, `run`, or `workout` can suggest `FITNESS`.
- Treat the mapping as a suggestion, not a reliable general-purpose NLP model. Resolve ambiguous or unknown input on the confirmation screen; do not invent an action or category.
- Users must be able to edit the suggested action and category before saving.

### 2.4 Date and Time-of-Day Parsing

- Support a deliberately small set of English terms such as `this morning`, `this afternoon`, `this evening`, `tonight`, and `yesterday`.
- Map broad periods such as `this morning` and `tonight` to a `TimeOfDay` value. Do not turn them into a fabricated exact timestamp.
- Store calendar dates as `LocalDate` values using the convention in `architecture.md`. If no date is expressed, default to the device's current local date and visibly show that default on the confirmation screen.
- Populate an exact `activityTimestamp` only when the text gives an unambiguous precise time and the parser can combine it with the activity date and device time zone safely. Otherwise, leave it null.
- If the date or period is uncertain, show the uncertainty or leave the value unset for user correction.

### 2.5 Confirmation Before Saving

- Never save a voice/text-derived activity automatically before the user confirms it.
- The confirmation screen must show editable values for action, category, duration (if any), activity date, and time of day (if detected).
- Distinguish extracted values from defaults where necessary so users can notice assumptions, especially a default date.
- Saving occurs only after the user confirms. Canceling or returning to editing must not create a database record.

## 3. Room Database and Core Entities

Use Room for local persistence. Keep the first schema small and explicit. Kotlin properties should use the types below where practical; TypeConverters must follow `architecture.md`.

### 3.1 `TaskEntity`

- `id`: `Long` primary key.
- `title`: `String`, required.
- `timeOfDay`: `TimeOfDay?`, optional planning period. It is not required in the MVP.
- `isCompleted`: `Boolean`.
- `taskDate`: `LocalDate`, stored as epoch-day `Long`.
- Do not add a `cue` field to tasks unless a concrete task feature requires it. In this MVP, `cue` belongs to habits.

### 3.2 `HabitEntity`

- `id`: `Long` primary key.
- `name`: `String`, required.
- `cue`: `String?`, optional reminder/context cue.
- `targetCompletionsPerWeek`: `Int`, constrained to `1..7` for the initial daily-habit model.
- `weeklyProgress` is the count of completions within the current calendar week compared against `targetCompletionsPerWeek`, using the week convention in Section 5.
- Calculate streaks from completion history. Do not persist `currentStreak`, `bestStreak`, or `lastCompletedDate` as duplicate sources of truth in the MVP.

### 3.3 `HabitCompletionEntity`

- `id`: `Long` primary key.
- `habitId`: foreign key to `HabitEntity`.
- `completedDate`: `LocalDate`, stored as epoch-day `Long`.
- Add a unique database constraint/index on `(habitId, completedDate)` to prevent duplicate completion records for the same habit and date.
- If permanent habit deletion is supported, make the treatment of associated completion history explicit in the UI and database relationship; do not silently discard that history.

### 3.4 `ActivityLogEntity`

- `id`: `Long` primary key.
- `rawInput`: `String`, the confirmed text transcript or typed input.
- `inputType`: enum with `TEXT` and `VOICE` values.
- `detectedAction`: `String`, the action confirmed by the user.
- `category`: one of the fixed categories in Section 2.3, confirmed by the user.
- `durationMinutes`: `Int?`, nullable when no duration is provided.
- `activityDate`: `LocalDate`, stored as epoch-day `Long`.
- `timeOfDay`: `TimeOfDay?`, nullable if not known.
- `activityTimestamp`: `Instant?`, stored as nullable epoch-millisecond `Long`; only populate when an exact time is known with sufficient confidence.
- `loggedAt`: `Instant`, stored as epoch-millisecond `Long`, representing when the record was saved.

Do not add additional fields or entities without a user story that needs them. Confirm required nullability and constraints against the UI flow before implementing the schema.

## 4. UI/UX Requirements

- Use Jetpack Compose and Material Design 3 components where appropriate (`Scaffold`, `TopAppBar`, `Card`, `FloatingActionButton`, and standard form controls).
- Enable edge-to-edge display and apply insets correctly.
- Provide clear empty states when there are no tasks, habits, or activity logs.
- Show validation and error feedback without losing user-entered text.
- Keep voice-derived data editable before persistence.
- Keep statistics derived from local records. Do not introduce a charting dependency unless the need is demonstrated and dependency changes are approved.

## 5. Statistics Requirements

- Display statistics in the `Progress` tab using local tasks, habit completions, and activity logs.
- Calculate daily and weekly task completion rate as `completedTasks / totalTasks * 100` for the selected period.
- If `totalTasks` is zero for the selected period, show a no-data or empty state instead of a numeric completion percentage.
- For weekly task statistics, weekly habit progress, and the `This Week` activity period, a calendar week runs from **Monday through Sunday**, using the device's local date.
- Display current and best habit streaks:
  - `currentStreak` is the number of consecutive calendar days with completion records ending today. If the habit has not been completed today, count the consecutive chain ending yesterday instead.
  - `bestStreak` is the maximum consecutive calendar-day chain in the habit's completion history.
- For `This Week`, `This Month`, and `Overall`, aggregate total `durationMinutes` and activity counts grouped by the fixed activity categories in Section 2.3.

## 6. Privacy Management

- Provide a `Profile` area where the user can inspect stored personal records for tasks, habits, and activity logs.
- Provide a **Delete All Personal Data** action with a confirmation dialog.
- Only after confirmation, completely wipe all local Room tables and reset app preferences, leaving no residual personal data in the app's local storage.

## 7. MVP Boundaries and Data Safety

- Prioritize task management, habit tracking, activity journaling with editable rule-based suggestions, the statistics in Section 5, and the privacy-management behavior in Section 6.
- Do not add account/login, cloud sync, remote NLP, analytics, or a backend unless the team approves a separate requirement.
- Do not silently drop database tables or reset user records during schema changes. Use data-preserving migrations and verify them when migrations are introduced. The explicit user-confirmed deletion feature in Section 6 is separate from schema migration behavior.
- Keep voice parsing and date/streak calculations deterministic and unit-testable.
