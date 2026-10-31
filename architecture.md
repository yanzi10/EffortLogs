# Application Architecture and Tech Stack

**Status:** Temporary project baseline. Keep this document aligned with the actual Gradle configuration and any decisions approved by the team.

## 1. Development Environment and SDK

- **IDE:** Android Studio.
- **Language:** Kotlin.
- **Build system:** Gradle Kotlin DSL (KTS) with the Version Catalog at `gradle/libs.versions.toml`.
- **Gradle JDK:** JDK 21 LTS, provided the project's existing Gradle and Android Gradle Plugin versions support it. Do not change the project's configured JDK or JVM targets without approval.
- **Compile SDK:** API 36 (Android 16), subject to the actual project configuration.
- **Target SDK:** API 36 (Android 16), subject to the actual project configuration.
- **Minimum SDK:** API 26 (Android 8.0). This is the project's chosen minimum supported Android version, not a claim that API 26 is required for every Java time API.
- Do not change SDK levels, toolchain versions, or build plugins without explicit approval or inclusion in the approved initial baseline described in `AGENTS.md`.

## 2. Architecture: Pragmatic MVVM

Use a single-activity application with MVVM, a clearly defined UI layer, and repositories for data access. Apply Clean Architecture principles where they reduce complexity; do not add layers for their own sake.

### UI / Presentation Layer

- **UI toolkit:** Jetpack Compose and Material Design 3.
- Build new screens using Compose; use components such as `Scaffold`, `TopAppBar`, `Card`, `FloatingActionButton`, and standard form controls where they suit the screen.
- Use screen-level ViewModels where they provide value. Expose screen state through `StateFlow` and collect it from Compose using lifecycle-aware collection.
- Represent relevant loading, content, empty, and error states explicitly.
- Enable edge-to-edge display and check system-bar insets so important content is not obscured.
- Use Jetpack Compose Navigation. Prefer type-safe routes only if the project's already configured Navigation version supports them; do not upgrade a dependency to obtain them without approval.

### Domain Layer

- Keep domain models and repository interfaces here when they clarify the feature boundary.
- Add a UseCase for complex, reusable, or independently testable business rules, such as streak calculation or voice-text parsing.
- Simple CRUD operations may call a repository from a ViewModel without an extra UseCase layer.

### Data Layer

- Use the Repository pattern to abstract local data sources.
- Use Room as the source of truth for tasks, habits, habit completions, and activity logs.
- The initial product is offline-first: app records are stored locally. Do not add remote synchronization or cloud processing without a separately approved requirement.

## 3. Core Libraries and Initial Baseline

The intended technology choices are:

- **UI:** Jetpack Compose and Material Design 3.
- **Navigation:** Jetpack Compose Navigation, with type-safe routes only where supported by the installed version.
- **Dependency injection:** Dagger Hilt.
- **Local database:** Room.
- **Asynchronous work and streams:** Kotlin Coroutines and Kotlin Flow.
- **Testing:** JUnit 4. Use fakes where practical; use MockK or Turbine only when already configured or separately approved.

This list is the intended initial stack, not proof that a library is installed. It becomes an initial dependency baseline only when the team explicitly approves the baseline plan described in `AGENTS.md`. Before coding, inspect what is installed and follow the dependency-approval rules in that document.

## 4. Feature-Based Source Structure

Use the actual package namespace from the Android project in place of `<actual_project_namespace>`.

```text
<actual_project_namespace>/
├── EffortLogApplication.kt       # @HiltAndroidApp entry point, if Hilt is configured
├── MainActivity.kt
├── core/
│   ├── designsystem/             # Theme, colors, typography and shared UI tokens
│   ├── database/                 # RoomDatabase setup and shared TypeConverters
│   ├── di/                       # App-wide bindings, e.g. the database provider
│   └── utils/                    # Small shared utilities and extensions
└── feature/
    ├── task/
    │   ├── data/
    │   │   ├── local/            # Feature entities and DAOs
    │   │   └── repository/       # Repository implementations
    │   ├── domain/               # Models and repository interfaces, if useful
    │   ├── presentation/         # Compose screens, ViewModels and UI state
    │   └── di/                   # Task-specific Hilt bindings
    ├── journal/
    │   ├── data/
    │   ├── domain/
    │   ├── presentation/
    │   └── di/
    ├── habit/
    │   ├── data/
    │   ├── domain/
    │   ├── presentation/
    │   └── di/
    ├── stats/
    │   ├── data/
    │   ├── domain/
    │   ├── presentation/
    │   └── di/
    └── profile/
        ├── data/
        ├── domain/
        ├── presentation/
        └── di/
```

This is a **package/folder organization inside the existing app module**, not a requirement to create separate Gradle modules. Feature-specific DI bindings belong in the feature's `di` package; app-wide infrastructure bindings belong in `core/di`. Create separate Gradle modules only after a deliberate team decision and approval.

## 5. Feature Boundaries and Shared Data

- A feature must not directly access another feature's `data` internals, including DAOs, entities, and repository implementations, or its `presentation` internals.
- Cross-feature aggregation, including `stats` and `profile`, must use domain repository interfaces or UseCases exposed by the feature that owns the data.
- Keep Room entities and DAOs near the feature that owns them where practical. The central `RoomDatabase` may reference entities and DAOs from multiple features as application-wide infrastructure.
- Shared converters and database configuration belong in `core/database`.
- Coordinate changes to shared files and schema work according to the ownership rules in `AGENTS.md`.

## 6. Room Data and Date/Time Conventions

Use strongly typed Kotlin properties (`LocalDate`, `Instant`, and enums) where practical, with Room TypeConverters mapping them to SQLite-supported values. The underlying stored values for date/time fields are integers or text according to these explicit conventions:

- **Calendar dates** such as `taskDate`, `activityDate`, and `completedDate` are `LocalDate` values. Store them as `Long` epoch days using `LocalDate.toEpochDay()`. An epoch day is a count of days, not milliseconds.
- **Exact instants** such as `loggedAt` are `Instant` values. Store them as `Long` epoch milliseconds using `Instant.toEpochMilli()`.
- **Optional exact activity time** (`activityTimestamp`) is an `Instant?`, stored as nullable epoch milliseconds. Leave it null if the input does not provide enough information to identify an exact instant; never invent a time from a phrase such as “this morning.”
- **Time-of-day labels** such as morning or evening use a `TimeOfDay` enum (or an equivalent agreed model), not epoch milliseconds.
- Use the device's local date for user-facing calendar behavior. Convert instants to local time only when displaying or interpreting them. Do not derive a habit's calendar date by truncating a UTC timestamp.
- Document converter behavior and keep it consistent across all features.

For habit tracking, store a completion record per habit per calendar date. Enforce uniqueness on `(habit_id, completed_date)` so the initial MVP records at most one completion per habit per day. Calculate current and best streaks from completion history according to `requirements.md`; do not store duplicate cached streak values unless profiling later demonstrates a real need and the team approves it.

## 7. Testing and Quality

- Use JUnit 4 for local unit tests and AndroidX/Compose testing APIs where appropriate and already configured.
- Prioritize tests for streak/date calculations, NLP parsing, ViewModel state transitions, repository behavior, and data integrity.
- Test database migrations when the project has migrations that need verification.
- Follow `AGENTS.md` for dependency approval, actual command execution, integration verification, and truthful reporting of test/build results.
