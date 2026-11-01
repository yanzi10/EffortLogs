---
name: android-dev
description: "Production-grade Android app development guide covering native Kotlin, pragmatic MVVM, Jetpack Compose, Room, Coroutines, Flow, and Hilt."
---

# Android App Development Skill

This skill provides production-grade architectural and implementation patterns for modern Android development, aligned with `sickn33/agentic-awesome-skills`.

## When to Use
- Developing native Android features in Kotlin.
- Designing architecture following Pragmatic MVVM and Clean Architecture principles.
- Implementing Room database persistence, DAOs, and migrations.
- Configuring and integrating Kotlin Coroutines, Flow, and Hilt Dependency Injection.

---

## 1. Architecture Principles (Pragmatic MVVM)

1. **Separation of Concerns:**
   - **UI / Presentation Layer:** Composables + ViewModels. ViewModels expose state via `StateFlow` and handle UI events.
   - **Domain Layer:** Optional and pragmatic. Create UseCases only when logic is complex, reusable, or state-heavy (e.g., streak calculations, parsing). Simple CRUD operations can directly invoke repositories.
   - **Data Layer:** Repositories abstract local Room databases and data sources. Never expose DAOs or raw entities across feature boundaries.
2. **Feature Boundaries:**
   - Do not directly access another feature's `data` internals (DAOs, entity tables, or repository implementations) or `presentation` internals. Cross-feature access must go through domain interfaces.
3. **Offline-First:**
   - Room is the single source of truth for persistent data. Keep queries reactive via `Flow<T>`.

---

## 2. Concurrency with Coroutines & Flow

- **Dispatchers:**
  - `Dispatchers.IO` for disk I/O, Room operations, and heavy serialization.
  - `Dispatchers.Default` for CPU-heavy transformations or list filtering.
  - `Dispatchers.Main` for UI interactions and ViewModel state emissions.
- **StateFlow vs SharedFlow:**
  - Use `StateFlow` for persistent UI state (has replay 1, holds current value, deduplicates identical consecutive emissions).
  - Use `SharedFlow` or one-off event channels for non-sticky transient events (navigation commands, snackbars).
- **Lifecycle Awareness:**
  - Collect flows in Compose using `collectAsStateWithLifecycle()` to automatically pause emissions when the lifecycle falls below `Lifecycle.State.STARTED`.

---

## 3. Data Integrity & Persistence (Room)

- **Date and Time Representation:**
  - Store dates/times strictly following project standards (e.g., ISO-8601 strings or epoch milliseconds); never treat a calendar date as an arbitrary instant.
- **Non-Destructive Migrations:**
  - Never introduce destructive migrations (`fallbackToDestructiveMigration`) in production. Always write explicit, tested `Migration` objects preserving user data.
- **Transactions:**
  - Group interdependent multi-row database updates in `@Transaction` blocks.

---

## 4. Privacy & Logging

- Never log personal or journal content, user input, or sensitive data to Logcat.
- Respect offline privacy requirements: do not add unapproved network telemetry, remote sync, or cloud NLP services.

