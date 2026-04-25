# Production Reference Branches

This branch is the cumulative production-grade reference for the Recall course app.
The original lesson, exercise, and solution branches remain optimized for course flow
and study.

## Branch Model

Each `prod-sol-*` branch is based directly on its matching `sol-*` branch. Production
branches are independent snapshots, not an incremental chain.

`prod-final-reference` is based on `sol-lesson-13b-crashlytics-remoteconfig` and carries
the complete production hardening set.

## Production Gate

Production reference branches are expected to pass the applicable local checks for the
modules present on that branch:

- `:shared:compileDebugKotlinAndroid`
- `:composeApp:assembleDebug`
- `:wear:assembleDebug`
- available unit test tasks for existing test sources
- install and launch smoke checks for `com.recall.app` and, when present, `com.recall.wear`

## Hardening Scope

The production branches preserve the lesson architecture visible at each point in the
course while tightening runtime behavior:

- owned Ktor client lifecycle and cancellation-safe networking
- lifecycle-scoped view model work
- empty-deck study flow handling
- non-blocking Wear tile and complication callbacks
- durable Room migrations and atomic deck refresh
- release versioning and Firebase configuration gates
