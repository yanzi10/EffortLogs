---
name: android-jetpack-compose-expert
description: "Expert guidance for building modern Android UIs with Jetpack Compose, covering state management, state hoisting, recomposition performance, and Material Design 3."
---

# Jetpack Compose Expert Skill

This skill provides comprehensive guidelines for building performant, maintainable, and idiomatic declarative UIs with Jetpack Compose and Material 3, aligned with `sickn33/agentic-awesome-skills`.

## When to Use
- Implementing new screens or UI components using Jetpack Compose.
- Managing Compose state, state hoisting, and side-effects.
- Auditing and optimizing recomposition performance and eliminating UI lag.
- Implementing edge-to-edge system bar handling and Material Design 3 components.

---

## 1. State Management & State Hoisting

1. **Unidirectional Data Flow (UDF):**
   - State flows down from ViewModel / parent composables to leaf components.
   - Events flow up from leaf composables via lambdas to ViewModels / handlers.
2. **Stateful vs Stateless Composables:**
   - Split complex components into a stateful container (responsible for ViewModel wiring, navigation, lifecycle collection) and a stateless presentational composable (accepts state data and emits callback lambdas).
   - This ensures stateless components can be previewed easily in `@Preview` and tested in isolation.
3. **Explicit UI State Modeling:**
   - Represent UI states as sealed interfaces or data classes with clear variants:
     ```kotlin
     sealed interface ScreenUiState {
         data object Loading : ScreenUiState
         data class Success(val items: List<ItemUiModel>) : ScreenUiState
         data object Empty : ScreenUiState
         data class Error(val message: String) : ScreenUiState
     }
     ```

---

## 2. Recomposition Performance & Stability

1. **Stable Lambdas & Method References:**
   - Avoid allocating new closures or lambdas directly inside recomposing scopes if passed to stable children. Prefer stable method references (e.g., `viewModel::onItemClick`) or `remember` where appropriate.
2. **Defer State Reads (Phase-Delay):**
   - If a state changes frequently (e.g., scroll offset, animations), read the state in the layout or draw phase rather than the composition phase:
     ```kotlin
     // Good: lambda modifier delays reading offset until draw phase
     Modifier.graphicsLayer { translationY = scrollOffsetProvider() }
     ```
3. **List Keying:**
   - Always provide a stable, unique `key` in `LazyColumn` / `LazyRow` items:
     ```kotlin
     items(items = state.items, key = { it.id }) { item ->
         ItemCard(item = item)
     }
     ```
4. **Derived State:**
   - Use `remember { derivedStateOf { ... } }` when deriving state from rapidly changing observable states (like scroll positions) to only recompose when the final derived condition changes.

---

## 3. Layout, Theming & System Insets

1. **Material Design 3:**
   - Use standard M3 design tokens (`MaterialTheme.colorScheme`, `MaterialTheme.typography`, `MaterialTheme.shapes`).
   - Employ `Scaffold`, `TopAppBar`, `FloatingActionButton`, and `Card` components consistently.
2. **Edge-to-Edge & Insets:**
   - Account for system bars (`statusBars`, `navigationBars`, `ime`) using `WindowInsets` or `Scaffold`'s `contentPadding` parameter to avoid content clipping behind system bars or virtual keyboards.

