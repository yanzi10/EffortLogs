---
name: systematic-debugging
description: "Use when encountering any bug, crash, test failure, or unexpected behavior. Enforces rigorous 4-phase root cause analysis before making code fixes."
---

# Systematic Debugging Skill

This skill enforces a disciplined, evidence-based debugging methodology, preventing trial-and-error guesswork, aligned with `sickn33/agentic-awesome-skills`.

## Core Principle
> **NO FIXES WITHOUT ROOT CAUSE INVESTIGATION FIRST.**
> Never write patches, suppress errors, or change code based on assumptions. First understand *why* the failure happens, reproduce it, and identify the root trigger.

---

## The Four-Phase Debugging Framework

### Phase 1: Root Cause Investigation
1. **Read the Full Error & Trace:**
   - Read every line of the stack trace, error code, and log output. Do not skim. Note the exact file, class, method, and line number.
2. **Reproduce Consistently:**
   - Determine exact reproduction steps and preconditions. Check whether it reproduces on every run or is intermittent / timing-dependent.
3. **Examine Recent Changes:**
   - Inspect `git diff` or recent commits that touched the failing subsystem.
4. **Trace Data Flow Upward:**
   - *Observe symptom:* Where does the crash or assertion error occur?
   * *Find immediate trigger:* What expression or variable is null/invalid at that line?
   * *Trace backward:* Where did that invalid value originate? Trace upstream through call chains to locate the true root cause.

### Phase 2: Pattern & Differential Analysis
1. **Find Working Counterparts:**
   - Look for similar features, DAOs, or ViewModels that work correctly in the codebase.
2. **Isolate Differences:**
   - Compare configurations, parameter types, threading contexts, or lifecycle states between working and broken components.

### Phase 3: Hypothesis Testing
1. **Formulate a Falsifiable Hypothesis:**
   - State clearly: *"I hypothesize the crash happens because X is called before Y completes, causing state Z."*
2. **Test Without Blind Edits:**
   - Use non-destructive checks, targeted assertions, or isolated test cases to confirm or disprove the hypothesis.

### Phase 4: Implementation & Prevention
1. **Write a Failing Test Case:**
   - When feasible, write a reproducing unit test first.
2. **Apply a Minimal, Precise Fix:**
   - Fix the root defect rather than masking it with null checks or empty fallbacks that hide underlying corruption.
3. **Verify Regression Safety:**
   - Execute the test suite to ensure existing functionality remains unbroken.
4. **Document Prevention:**
   - Ensure the error state is handled cleanly and documented for future maintainers.

