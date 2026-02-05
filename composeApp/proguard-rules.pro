# ProGuard rules for Recall App
# These rules preserve classes and methods that use reflection (Room, Koin)
# R8/ProGuard would otherwise strip them during code shrinking

# TODO 4: Add ProGuard rules to prevent R8 from breaking reflection-dependent libraries
#
# Your app uses libraries that rely on reflection and runtime code generation:
# - Room Database (@Entity, @Dao, @Database annotations)
# - Koin Dependency Injection (needs Kotlin Metadata for type resolution)
# - Kotlin Serialization (needs $$serializer classes and Companion objects)
#
# Without proper -keep rules, R8 will aggressively remove these classes, causing runtime crashes.
#
# TASK: Add the following types of rules:
#
# 1. Room Database Rules:
#    - Keep all Room base classes: -keep class androidx.room.** { *; }
#    - Keep @Entity annotated classes: -keep @androidx.room.Entity class * { *; }
#    - Keep @Dao annotated interfaces: -keep @androidx.room.Dao class * { *; }
#    - Keep @Database annotated classes: -keep @androidx.room.Database class * { *; }
#    - Keep your app's database package: -keep class com.recall.app.data.local.** { *; }
#
# 2. Koin Dependency Injection Rules:
#    - Keep Koin core classes: -keep class org.koin.** { *; }
#    - Keep Kotlin Metadata: -keep class kotlin.Metadata { *; }
#    - Keep your DI modules: -keep class com.recall.app.di.** { *; }
#
# 3. Kotlin Serialization Rules (if using serialization):
#    - Keep serializer classes: -keep,includedescriptorclasses class com.recall.app.**$$serializer { *; }
#    - Keep Companion objects: -keepclassmembers class com.recall.app.** { *** Companion; }
#
# 4. SLF4J Logging (suppress warnings):
#    - Add -dontwarn rules for optional SLF4J implementations
#
# 5. General Android:
#    - Keep native methods: -keepclasseswithmembernames class * { native <methods>; }
#
# Example format:
# -keep class package.name.ClassName { *; }
# -keep @annotation.Name class * { *; }
# -dontwarn package.name.**
#
# Add your rules below this line:
# ============================================================
