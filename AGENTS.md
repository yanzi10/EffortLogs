# Agent Rules for Effort Log

These rules define project-specific expectations for AI coding agents. They complement, but do not replace, Antigravity's execution permissions and artifact review settings.

## 1. Project Context and Scope

- Before changing code, inspect the relevant project structure, actual package namespace, Gradle modules and wrapper, plugin versions, `gradle/libs.versions.toml`, `AndroidManifest.xml`, architecture documentation, and relevant existing code.
- Check `git status --short` before editing. Preserve all pre-existing user changes, including uncommitted changes. Never discard, overwrite, or revert them merely to obtain a clean working tree.
- Treat the existing build configuration as the source of truth for what is currently installed. Documentation describes the agreed target architecture; it does not prove that a dependency is already installed.
- Keep changes focused on the requested task. Do not edit unrelated features or perform unrelated cleanup.
- If a requirement conflicts with the existing code, build configuration, or another project document, explain the conflict and propose the smallest safe resolution before making a significant change.
- Do not assume a package name such as `com.example.effortlog`; use the actual namespace in the project.

## 2. Planning and Approval

- Use Antigravity Planning Mode and `/plan` for Room schema changes or migrations, architectural changes, permission changes, high-impact work, and work that changes shared feature boundaries. Present a concrete plan with affected files, implementation steps, risks, and verification steps.
- Wait for explicit approval of the plan before implementing it. If `/plan` is unavailable, write the plan in the conversation and wait for approval.
- Fast Mode is appropriate for small, localized, low-risk changes. A straightforward multi-file change with clear requirements does not need the full plan-approval cycle when it does not change architecture, shared configuration, permissions, or stored data.
- Approval of a plan does not automatically authorize unmentioned dependency changes, new permissions, destructive data operations, or changes outside the approved scope.
- Do not modify `minSdk`, `targetSdk`, `compileSdk`, Gradle/Android Gradle Plugin/Kotlin plugin versions, JVM targets, dependency versions, or the database migration strategy without explicit approval, except for changes explicitly included in an approved initial baseline under Section 3.

## 3. Dependency Management

- **STRICT: Never add, update, or remove a dependency without explicit user approval.** This includes changes to `build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`, plugin declarations, and dependency-related configuration. The only exception is implementation of the exact changes covered by an explicitly approved initial baseline plan.
- For initial project setup, the team may approve the intended stack in `architecture.md` Section 3 and its corresponding Gradle and version-catalog changes as one baseline plan. The approval must identify the libraries and configuration covered. After that approval, implementing exactly that baseline does not require separate approval for each listed dependency.
- Any dependency outside the approved baseline, and any later addition, update, or removal, still requires explicit approval.
- If a feature needs a dependency that is not already configured or approved, explain why it is needed, name the proposed library, describe a reasonable alternative (including a no-new-dependency option when practical), and wait for approval.
- Do not assume that listing a library in `architecture.md` authorizes editing Gradle files. Check whether it is already present and whether the initial baseline was explicitly approved.
- Do not upgrade a library merely to use a newer API or coding style. First check whether the current version supports the needed behavior.

## 4. Architecture and Kotlin Practices

- Use Kotlin and Jetpack Compose with Material Design 3 for new UI. Do not add XML layouts for new screens unless explicitly requested or required to maintain an existing integration.
- Follow pragmatic MVVM and the agreed feature-based source structure in `architecture.md`.
- Keep screen state in a `ViewModel` where appropriate and expose observable state as `StateFlow`. Collect UI state in a lifecycle-aware way from Compose.
- Use repositories to abstract data sources. Create a UseCase only when logic is complex, reusable, or meaningfully clearer as a separate unit; do not create a UseCase for every simple CRUD operation.
- Keep `ViewModel` classes independent of `Activity` and `Context` where practical. Do not put database queries directly in Composables.
- Put feature-specific Hilt bindings in the owning feature's `di` package. Put application-wide bindings, such as the Room database, in `core/di`. This package organization does not create separate Gradle modules; do not create Gradle modules without approval.
- **STRICT: Never access another feature's `data` internals (DAOs, entities, or repository implementations) or `presentation` internals directly.** Cross-feature communication, including statistics and personal-data aggregation, must go through domain repository interfaces or UseCases exposed by the owning feature.
- Prefer simple, readable Kotlin over speculative abstractions or premature optimization.
- Refactor only within the requested scope when the improvement is clear and behavior-preserving. Ask before broad refactoring or restructuring.

## 5. Testing and Verification

- Use **JUnit 4** as the project's standard test framework. Do not introduce JUnit 5 for this project without explicit approval.
- Add meaningful unit tests for new ViewModels, core business logic, and complex or reusable UseCases. Add repository/data-layer tests where behavior or data integrity warrants them.
- Prefer small fake implementations when practical. Use MockK or Turbine only if already configured and useful, or after the user explicitly approves adding them. Do not require every test to use a mocking or Flow-testing library.
- For Compose UI behavior, use the existing Compose testing setup when appropriate and available. Do not add test dependencies without approval.
- **Before reporting that a test passed, execute the relevant Gradle test task in the terminal and inspect its actual output.** Do not infer success from code inspection, a generated test file, or a previous run.
- On Windows, use the project's Gradle Wrapper, commonly `.\gradlew.bat testDebugUnitTest` (PowerShell). On macOS/Linux, commonly use `./gradlew testDebugUnitTest`. Inspect the project tasks and module names rather than assuming these exact task names always exist.
- Run the relevant tests and, where practical, the appropriate build task. If a test/build cannot be executed because of a missing SDK, unavailable emulator, environment issue, or other blocker, report the exact blocker and the checks that were not run.
- Never claim that tests or builds succeeded unless they actually completed successfully. Do not modify unrelated code simply to suppress an error.

## 6. Data Integrity, Privacy, and Error Handling

- Never introduce a destructive Room migration that drops tables or silently deletes user data. Preserve existing records during schema changes. If a destructive reset is genuinely necessary during development, explain its impact and obtain explicit approval first.
- Add and test a Room migration when changing a schema that may already contain user data. Do not silently change the database version or migration strategy.
- Follow the date/time representation rules in `architecture.md` and `requirements.md`; do not treat a calendar date as an instant.
- Handle Loading, Content/Success, Empty, and Error states where relevant. Handle permission denial, cancellation, unavailable speech services, malformed input, and failed recognition without crashing.
- Do not log journal text, recognized speech, or other sensitive personal content to Logcat. Do not add remote storage, analytics, telemetry, cloud NLP, or network transmission of journal content without explicit approval.
- Do not silently switch to a speech-recognition path that may send audio to a remote service. Follow the consent and fallback behavior in `requirements.md`.

## 7. Documentation and Communication

- Write code comments, KDoc, project documentation, and Git commit messages in English.
- Use Conventional Commits for commit messages, such as `feat: add habit completion tracking`, `fix: handle unavailable speech recognition`, or `test: cover streak calculation`.
- Explain important assumptions and trade-offs plainly. In conversation, use Vietnamese by default unless the user requests another language.
- Do not claim that a feature is complete when required behavior, tests, or verification steps remain unfinished.

## 8. Team Coordination for Parallel Work

- Link each implementation task to a user story or concrete acceptance criteria before coding.
- Organize team work as one branch or pull request per small task. This workflow does not authorize an agent to commit or push unless explicitly requested under Section 10.
- Assign clear owners for shared files and configuration, including Gradle files, `RoomDatabase`, navigation, and `KeywordDictionary`. Coordinate before changing a shared area owned by another active task.
- Do not let parallel tasks independently increment the database version or create competing migrations. Coordinate schema changes through the assigned database owner.
- Review the relevant diff and run the relevant tests after integration, not only on the individual task branch.

## 9. Data and Documentation Ownership

- `AGENTS.md` defines how agents work and how the team coordinates changes.
- `architecture.md` defines the intended technical structure and data representation.
- `requirements.md` defines product behavior and MVP acceptance requirements.
- Avoid repeating detailed rules across these documents. When responsibilities change, update the owning document and keep cross-references accurate.

## 10. Git Operations

- Do not create commits, push, rebase, cherry-pick, reset, clean, stash, or rewrite Git history unless explicitly requested by the user.
- Never use destructive commands such as `git reset --hard`, `git clean -fd`, or force-push as routine recovery steps. Explain the consequences and obtain explicit approval first.
- Do not overwrite or revert another contributor's work. Review the relevant diff and preserve unrelated changes.
- Do not stage files automatically unless the user explicitly asks you to stage them.

## 11. Completion Report

After implementing a task, report:

1. The files and behavior changed.
2. Important implementation choices or assumptions.
3. The tests/build commands actually run and their results.
4. Any unresolved issues, limitations, or actions that still need approval.
