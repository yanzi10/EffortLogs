---
name: code-review
description: "Code review checklist and quality audit guidelines for Kotlin, Android architecture, coroutine safety, and privacy boundaries."
---

# Code Review Skill

This skill provides a structured checklist for auditing Kotlin and Android code changes, preventing anti-patterns, regressions, and privacy leaks, aligned with `sickn33/agentic-awesome-skills`.

## When to Use
- Reviewing pull requests or staging changes before committing.
- Auditing new code for architectural conformance and boundary violations.
- Checking for coroutine memory/lifecycle leaks and thread safety.
- Ensuring privacy compliance (no sensitive data logging).

---

## Code Review Checklist

### 1. Architectural Boundaries & Clean Architecture
- [ ] Are feature boundaries respected? No direct imports of another feature's internal DAOs, entities, or private presentation models.
- [ ] Is data access abstracted behind Repository interfaces?
- [ ] Are ViewModels free of direct Android `Context` or `Activity` references?
- [ ] Are UI components purely declarative with no direct database queries inside composables?

### 2. Coroutines & Concurrency Safety
- [ ] Are appropriate dispatchers used (`Dispatchers.IO` for disk/database, `Dispatchers.Default` for heavy calculations)?
- [ ] Are coroutines launched within appropriate lifecycle scopes (`viewModelScope` in ViewModels)?
- [ ] Are exceptions in Flows handled using `.catch { ... }`?
- [ ] Are hot flows (`SharedFlow`, `StateFlow`) configured with proper replay and buffer overflow strategies?

### 3. Jetpack Compose Quality
- [ ] Is UI state exposed as `StateFlow` and collected using `collectAsStateWithLifecycle()`?
- [ ] Are list items in `LazyColumn`/`LazyRow` keyed using a stable, unique identifier?
- [ ] Are state hoisting principles applied (separating stateful container from stateless UI)?
- [ ] Are system bar insets (`Scaffold` content padding) respected?

### 4. Privacy & Error Resilience
- [ ] **STRICT:** Is user journal text, speech-to-text transcript, or personal data kept out of Logcat?
- [ ] Are all UI states represented (Loading, Content, Empty, Error)?
- [ ] Are database operations non-destructive, with proper schema migrations?
- [ ] Are permission denials and hardware unavailability (speech service, microphone) handled gracefully without crashing?

