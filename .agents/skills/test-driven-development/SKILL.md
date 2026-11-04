---
name: test-driven-development
description: "Guidelines for writing reliable unit, integration, and UI tests following TDD principles and JUnit 4 standards."
---

# Test-Driven Development (TDD) Skill

This skill provides best practices for writing maintainable, deterministic tests for Android, ViewModels, business logic, and UI, aligned with `sickn33/agentic-awesome-skills`.

## When to Use
- Implementing new business logic, UseCases, or repository operations.
- Verifying ViewModel state emissions and coroutine flows.
- Writing Room DAO unit tests with in-memory databases.
- Implementing UI regression tests using Jetpack Compose testing.

---

## 1. The TDD Cycle (Red-Green-Refactor)

1. **Red:** Write a small, focused test describing the desired behavior before writing the production code. Run the test to verify that it fails for the expected reason.
2. **Green:** Write the minimal implementation required to pass the test. Do not over-engineer.
3. **Refactor:** Clean up the implementation, improve naming and abstraction, while keeping all tests green.

---

## 2. Unit Testing Guidelines

1. **Framework:**
   - Use **JUnit 4** (standard `@Test` annotation). Do not introduce JUnit 5 unless approved.
2. **Naming Convention:**
   - Use descriptive test names reflecting behavior and expectations:
     `methodUnderTest_condition_expectedResult`
     or backtick notation in Kotlin:
     `@Test fun calculateStreak_withMissedDay_resetsToZero()`
3. **Test Doubles:**
   - Prefer small, predictable fake implementations (e.g., `FakeTaskRepository`) over heavy dynamic mocking where practical.
4. **Testing Coroutines & StateFlow:**
   - Use `runTest` from `kotlinx.coroutines.test`.
   - Inject a `TestDispatcher` (e.g., `StandardTestDispatcher` or `UnconfinedTestDispatcher`) into ViewModels/UseCases instead of hardcoding `Dispatchers.IO` or `Dispatchers.Main`.
   - Use `advanceUntilIdle()` to execute pending coroutines predictably.

---

## 3. Room Database & DAO Testing

- Use `Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()` for fast, isolated DAO tests.
- Close the database instance in the `@After` teardown method.

---

## 4. Jetpack Compose UI Testing

- Use `ComposeContentTestRule` (`createComposeRule()`).
- Target elements via semantic node matchers (`hasTestTag(...)`, `hasText(...)`, `hasContentDescription(...)`).
- Avoid arbitrary sleeps (`Thread.sleep`); allow Compose's built-in synchronization to wait for idle state.

