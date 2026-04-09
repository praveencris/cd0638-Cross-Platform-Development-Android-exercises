# Course Branch Quality Fixes — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fix all course quality issues across 51 branches so every starter branch compiles and builds a debug APK, every solution branch matches its TODOs, and the lesson-to-lesson progression is clean.

**Architecture:** Each task targets a specific category of fix applied across multiple branches via scripted batch edits. All changes are committed per-branch. Final validation runs compile + assembleDebug across all 51 branches.

**Tech Stack:** Kotlin Multiplatform, Gradle, Room KSP, Ktor, Koin, Compose Multiplatform, Wear OS Compose

---

## Current State Summary

- **51 total branches**: 1 main, 1 initial-setup, 1 exer-lesson-1, 24 lesson starters (lesson-*), 24 solution branches (sol-lesson-*)
- **39 branches** have unpushed "harden" commits (added timeouts, CancellationException rethrow, JVM 1.8→11)
- **7 solution branches** have a broken import introduced by harden commits (sol-lesson-7a through 7d, sol-lesson-8a through 8c)
- **2 stashes** exist (stash@{0} on sol-lesson-1, stash@{1} on lesson-6a)
- **lesson-8a starter** is missing the Room KSP fix we applied to 8b+ branches
- All starter branches use JVM_1_8 in shared/build.gradle.kts; all solutions use JVM_11 (diff noise)
- shared/build.gradle.kts hardcodes compileSdk=34; composeApp uses version catalog (36)

---

## Phase 1: Clean Up Unpushed Harden Commits (PREREQUISITE)

> **Decision needed from user:** The "harden" commits were applied by a previous Claude session. They contain useful changes (timeouts, CancellationException fix) but also introduced a broken import on 7 branches. We have two options:
>
> **Option A: Revert all harden commits, redo them cleanly.**
> Pros: Clean slate, no broken imports, we control what goes in.
> Cons: More work, need to re-apply the useful parts (timeouts, CancellationException).
>
> **Option B: Keep harden commits, fix the 7 broken imports on top.**
> Pros: Less work, useful changes already in place.
> Cons: The harden commits are inconsistent (different commit messages, some branches have 2 commits, some have 1). JVM 1.8→11 is in harden commits for starters but should be its own fix.
>
> **Recommendation: Option A.** Revert all harden commits for a clean slate. Then apply JVM_11 and compileSdk fixes as part of Phase 2, and re-apply the timeout/CancellationException fixes cleanly in Phase 3.

### Task 1.1: Drop stashes

**Scope:** 2 stashes

- [ ] Drop stash@{0} (broken Platform.kt edits on sol-lesson-1)
- [ ] Drop stash@{1} (unknown edits on lesson-6a)
- [ ] Verify `git stash list` is empty

### Task 1.2: Revert all unpushed harden commits

**Scope:** 39 branches

- [ ] Write script that for each branch with unpushed "harden" commits: resets to the commit BEFORE the first harden commit (using `git log origin/<branch>..<branch>` to find them)
- [ ] Also revert the non-harden unpushed commits:
  - README updates on sol-lesson-3, sol-lesson-7b, sol-lesson-11-android
  - .gitignore keystores on lesson-12a, 12b, sol-12a, sol-12b
  - Timeout-only commit on lesson-3
  - Commented-out code commit on lesson-3 (our recent fix)
  - Commented-out code commit on lesson-4a (our recent fix)
- [ ] Do NOT revert:
  - Room KSP fix commits on lesson-8b+ and sol-lesson-8a+ (these are correct fixes)
  - The main branch commits (initial setup)
- [ ] Verify all branches match origin except those with Room KSP fixes

### Task 1.3: Verify clean state

- [ ] Run: `git branch --list | while read branch; do AHEAD=$(git rev-list --count origin/$branch..$branch); if [ $AHEAD -gt 0 ]; then echo "$branch: $AHEAD ahead"; fi; done`
- [ ] Only Room-KSP-fixed branches and main should be ahead of origin
- [ ] Run compile check on all 51 branches to establish baseline

---

## Phase 2: Infrastructure Fixes (Apply to ALL branches)

These are build infrastructure changes that should be consistent across every branch.

### Task 2.1: Fix JVM target — all starters to JVM_11

**Scope:** All starter branches that still use JVM_1_8 in shared/build.gradle.kts
**File:** `shared/build.gradle.kts` on each branch

The solution branches already have JVM_11 (from harden commits we'll revert, then from this fix).

- [ ] Write Python script to:
  1. Checkout each starter branch
  2. In `shared/build.gradle.kts`: replace `JvmTarget.JVM_1_8` with `JvmTarget.JVM_11`, replace `JavaVersion.VERSION_1_8` with `JavaVersion.VERSION_11`
  3. Commit: "Align JVM target to 11 in shared module"
  4. Verify compile
- [ ] Starter branches to fix (those that have JVM_1_8):
  - lesson-1-project-setup, exer-lesson-1-project-setup
  - lesson-2, lesson-3, lesson-4a, lesson-4b
  - lesson-5a, lesson-5b, lesson-6a, lesson-6b
  - lesson-7a, lesson-7b, lesson-7c, lesson-7d
  - lesson-8a, lesson-8b, lesson-8c
  - lesson-9, lesson-10
  - lesson-11-android, lesson-11-ios
  - lesson-12a, lesson-12b
  - lesson-13a, lesson-13b
- [ ] Also fix on solution branches IF they lost JVM_11 after harden revert:
  - All sol-lesson-* branches from 3 onward
- [ ] Do NOT touch: initial-setup, main (check their current state first)

### Task 2.2: Fix compileSdk mismatch — shared module to use version catalog

**Scope:** All branches where `shared/build.gradle.kts` hardcodes `compileSdk = 34`
**File:** `shared/build.gradle.kts` on each branch

- [ ] Replace `compileSdk = 34` with `compileSdk = libs.versions.android.compileSdk.get().toInt()` on all branches
- [ ] This requires the `[versions]` section to have `android-compileSdk` — verify it exists on each branch's `gradle/libs.versions.toml` first
- [ ] Commit: "Use version catalog for shared module compileSdk"
- [ ] Verify compile

### Task 2.3: Apply Room KSP fix to lesson-8a starter

**Scope:** `lesson-8a-db-foundation-setup` only
**Files:**
- `gradle/libs.versions.toml` — add `room = { id = "androidx.room", version.ref = "room" }` to [plugins]
- `shared/build.gradle.kts` — add `alias(libs.plugins.room)`, add `room { schemaDirectory(...) }`, remove `kspCommonMainMetadata`

- [ ] Read `lesson-8a-db-foundation-setup:shared/build.gradle.kts` to understand current state
- [ ] Apply the same pattern as sol-lesson-8a (but keep the TODOs in RecallDatabase.kt — students need to add `@ConstructedBy` themselves)
- [ ] NOTE: Do NOT add `override fun initialize()` to expect object on lesson-8a — the expect object doesn't exist yet in the starter (students create it in TODO 4). Only ensure the build infrastructure (plugin, schemaDirectory) is present so it compiles once students add their code.
- [ ] Commit: "Add Room Gradle plugin to build infrastructure"
- [ ] Verify compile

---

## Phase 3: Re-apply Useful Harden Changes Cleanly

After Phase 1 revert and Phase 2 infrastructure fixes, re-apply the genuinely useful parts of the harden commits.

### Task 3.1: Add network timeouts to NetworkClient.kt

**Scope:** All branches from lesson-3 onward that have a NetworkClient.kt with HttpClient
**File:** `shared/src/commonMain/kotlin/com/recall/app/network/NetworkClient.kt`

- [ ] On solution branches (sol-lesson-3 through sol-lesson-13b): add `install(HttpTimeout)` block if not present
- [ ] On starter branches where NetworkClient.kt is NOT commented out (lesson-4b onward): add timeout block
- [ ] On lesson-3 starter: the file is commented out, so skip
- [ ] Commit: "Add network request timeouts to prevent indefinite hangs"
- [ ] Be careful NOT to introduce the broken import — ensure each import is on its own line

### Task 3.2: Fix CancellationException handling

**Scope:** All branches from lesson-4 onward that have try/catch blocks in ViewModels or repositories
**Files:** `MainViewModel.kt`, any Repository classes

- [ ] In catch blocks that catch `Exception`, add `CancellationException` rethrow:
  ```kotlin
  } catch (e: kotlin.coroutines.cancellation.CancellationException) {
      throw e
  } catch (e: Exception) {
  ```
- [ ] Only apply where the pattern exists and makes sense
- [ ] Commit: "Rethrow CancellationException to preserve structured concurrency"

---

## Phase 4: Fix Lesson-Specific Issues

### Task 4.1: Fix lesson-3 starter (commented-out code)

**Scope:** `lesson-3-networking-with-ktor`
**Context:** After Phase 1 revert, lesson-3 will be back to origin state (broken — code imports Ktor but deps are commented out). We need to re-apply our fix.

- [ ] Comment out all Ktor-importing code in:
  - `shared/src/commonMain/kotlin/com/recall/app/network/NetworkClient.kt`
  - `shared/src/commonMain/kotlin/com/recall/app/network/RecallApiService.kt`
  - `shared/src/androidMain/kotlin/com/recall/app/network/NetworkClient.android.kt`
  - `shared/src/iosMain/kotlin/com/recall/app/network/NetworkClient.ios.kt`
  - `shared/src/commonTest/kotlin/com/recall/app/network/RecallApiTest.kt`
- [ ] Keep `// TODO: Uncomment after adding Ktor dependencies` pattern
- [ ] Commit: "Comment out Ktor-dependent code to match commented-out dependencies"
- [ ] Verify compile + assembleDebug

### Task 4.2: Fix lesson-4a starter (commented-out code)

**Scope:** `lesson-4a-consuming-di-handshake`
**Context:** After Phase 1 revert, lesson-4a will be back to origin state (broken — code imports shared/Koin but deps are TODOs).

- [ ] Comment out:
  - `composeApp/src/commonMain/kotlin/com/recall/app/MainViewModel.kt`
  - `composeApp/src/androidMain/kotlin/com/recall/app/di/AndroidModule.kt`
- [ ] Keep `// TODO: Uncomment after linking :shared module and adding DI dependencies` pattern
- [ ] Commit: "Comment out code that depends on :shared module and Koin"
- [ ] Verify compile + assembleDebug

### Task 4.3: Fix lesson-8a TODO hint mismatch (exportSchema)

**Scope:** `lesson-8a-db-foundation-setup`
**File:** `shared/src/commonMain/kotlin/com/recall/app/data/local/RecallDatabase.kt`

- [ ] Read the TODO hint — it says `exportSchema = true`
- [ ] Read the solution — it uses `exportSchema = false`
- [ ] Change the TODO hint to match the solution: `exportSchema = false` with a comment explaining why
- [ ] Commit: "Fix exportSchema TODO hint to match solution"

### Task 4.4: Fix sol-lesson-8a — remove `-Xexpect-actual-classes` if unnecessary

**Scope:** `sol-lesson-8a-db-foundation-setup`
**File:** `shared/build.gradle.kts`

- [ ] Check if sol-lesson-8a has `-Xexpect-actual-classes` flag that no other branch has
- [ ] If `@Suppress("NO_ACTUAL_FOR_EXPECT")` is sufficient (it is on all other branches), remove the flag for consistency
- [ ] Verify compile after removal

---

## Phase 5: Validate Everything

### Task 5.1: Full compile validation

- [ ] Run `compileCommonMainKotlinMetadata` on all 51 branches
- [ ] All must pass (skip podInstallSyntheticIos on Firebase branches)

### Task 5.2: Full APK build validation

- [ ] Run `assembleDebug` on all 51 branches
- [ ] All must pass except Firebase branches which need google-services.json (env setup)

### Task 5.3: Diff sanity check

- [ ] For a sample of lesson pairs (1, 3, 4a, 8a, 8b, 10, 13a), run `git diff lesson-X..sol-lesson-X --stat`
- [ ] Verify the diffs look reasonable:
  - No JVM_1_8→11 noise (should be same in both)
  - No compileSdk noise
  - No harden commit noise
  - Only actual lesson content differs

---

## Issues NOT Fixed in This Plan (Deferred)

The following issues from the course review are **pedagogical/content** issues, not build issues. They require curriculum decisions from the course author and are deferred to a separate plan:

1. **TODO/solution mismatches in lesson content:**
   - lesson-1 README hint vs solution approach
   - lesson-2 README overpromising @Serializable
   - lesson-3 sol README wrong property names
   - lesson-7b permission approach mismatch (ActivityResultContracts vs Accompanist)
   - lesson-12b path mismatches in Fastlane hints

2. **Missing TODO stubs:**
   - lesson-8c has no explicit TODOs (DeckWithCards, DAO method, Repository)
   - lesson-10 AccessibilityAnnouncer expect/actual not scaffolded
   - lesson-8a Result.kt appears in solution with no TODO

3. **Exercise scope issues:**
   - lesson-5b DeckCard.kt single-line TODO → 220 lines of solution
   - lesson-6b tries to cover too many concepts
   - lesson-7b spans too many files/concepts
   - lesson-12a/12b exercises too shallow

4. **Architectural issues:**
   - lesson-4a→4b ViewModel rewrite / Koin version jump
   - lesson-4b coroutine scope leak
   - lesson-6b code duplication in rating buttons

These should be addressed in a follow-up conversation focused on curriculum content.

---

## Execution Order

```
Phase 1 (Clean slate)
  └─ 1.1: Drop stashes
  └─ 1.2: Revert harden + other unpushed commits
  └─ 1.3: Verify clean state
Phase 2 (Infrastructure — batch across all branches)
  └─ 2.1: JVM_11 on all starters + solutions
  └─ 2.2: compileSdk version catalog on all branches
  └─ 2.3: Room KSP fix on lesson-8a starter
Phase 3 (Re-apply useful changes)
  └─ 3.1: Network timeouts (solution + active starter branches)
  └─ 3.2: CancellationException fix
Phase 4 (Lesson-specific)
  └─ 4.1: lesson-3 commented-out code
  └─ 4.2: lesson-4a commented-out code
  └─ 4.3: lesson-8a exportSchema hint
  └─ 4.4: sol-lesson-8a Xexpect-actual-classes flag
Phase 5 (Validation)
  └─ 5.1: Compile all 51
  └─ 5.2: APK build all 51
  └─ 5.3: Diff sanity check
```

**Estimated scope:** ~51 branches × 2-4 edits per phase = ~150-200 file edits, all scriptable.
