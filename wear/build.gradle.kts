plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    kotlin("android")
}

android {
    namespace = "com.recall.wear"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.recall.wear"
        minSdk = 30 // Wear OS 3.0
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,LICENSE-notice.md}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Shared module - reuse business logic
    implementation(project(":shared"))

    // Wear OS Compose
    implementation(libs.androidx.wear.compose.material)
    implementation(libs.androidx.wear.compose.foundation)
    implementation(libs.androidx.wear.compose.navigation)

    // Wear OS Tiles - Quick access glanceable surfaces
    implementation(libs.androidx.wear.tiles)

    // Wear OS Complications - Watch face integration
    implementation(libs.androidx.wear.watchface.complications.data.source)

    // Health Services - Access to health and fitness data
    implementation(libs.androidx.health.services.client)

    // Guava - Required by Health Services for ListenableFuture
    implementation(libs.guava)

    // Horologist - Compose layout helpers for Wear OS
    implementation(libs.horologist.compose.layout)
    implementation(libs.horologist.composables)

    // Google Play Services
    implementation(libs.play.services.wearable)

    // Koin for dependency injection
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    // Accompanist for permission handling in Compose
    implementation(libs.accompanist.permissions)

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${libs.versions.kotlinx.coroutines.get()}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${libs.versions.kotlinx.coroutines.get()}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:${libs.versions.kotlinx.coroutines.get()}")

    // Unit Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)

    // Android Testing
    androidTestImplementation(libs.androidx.testExt.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.mockk.android)
}
