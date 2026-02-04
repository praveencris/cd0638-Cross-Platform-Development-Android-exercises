# ProGuard rules for Recall App
# These rules preserve classes and methods that use reflection (Room, Koin)
# R8/ProGuard would otherwise strip them during code shrinking

# ============================================================
# Room Database
# ============================================================
# Room uses annotation processing and reflection for query generation
# Keep all database-related classes to prevent runtime crashes

# Keep Room base classes
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Keep all @Entity annotated classes (database tables)
-keep @androidx.room.Entity class * { *; }

# Keep all @Dao annotated interfaces (data access objects)
-keep @androidx.room.Dao class * { *; }

# Keep all @Database annotated classes (database definition)
-keep @androidx.room.Database class * { *; }

# Keep Recall app's database entities and DAOs
-keep class com.recall.app.data.local.entity.** { *; }
-keep class com.recall.app.data.local.dao.** { *; }
-keep class com.recall.app.data.local.RecallDatabase { *; }

# ============================================================
# Koin Dependency Injection
# ============================================================
# Koin uses reflection for dependency resolution
# Keep Koin classes and Kotlin metadata for proper DI

# Keep Koin core classes
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Keep Kotlin metadata (used by Koin for type resolution)
-keep class kotlin.Metadata { *; }

# Keep Koin module definitions
-keep class com.recall.app.di.** { *; }

# ============================================================
# Kotlin Coroutines
# ============================================================
# Keep coroutines for proper async operation
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# ============================================================
# Kotlin Serialization (if used by Ktor)
# ============================================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Serializable classes
-keep,includedescriptorclasses class com.recall.app.**$$serializer { *; }
-keepclassmembers class com.recall.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.recall.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ============================================================
# General Android
# ============================================================
# Keep native methods (used by JNI)
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
