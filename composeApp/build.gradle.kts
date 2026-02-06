import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.compose)
        }
        commonMain.dependencies {
            implementation(project(":shared"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.multiplatform)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.recall.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.recall.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // Signing configuration for release builds
    // Credentials are injected via environment variables to keep them out of version control
    signingConfigs {
        create("release") {
            // Keystore file path from environment or local.properties
            val keystorePath = System.getenv("KEYSTORE_PATH")
                ?: project.findProperty("keystorePath") as String?

            // Only configure signing if keystore path is provided
            if (keystorePath != null) {
                storeFile = file(keystorePath)

                // Keystore password
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                    ?: project.findProperty("keystorePassword") as String?

                // Key alias
                keyAlias = System.getenv("KEY_ALIAS")
                    ?: project.findProperty("keyAlias") as String?

                // Key password
                keyPassword = System.getenv("KEY_PASSWORD")
                    ?: project.findProperty("keyPassword") as String?
            }
        }
    }

    buildTypes {
        getByName("release") {
            // Enable R8 code shrinking, obfuscation, and optimization
            isMinifyEnabled = true

            // Remove unused resources (drawables, layouts, etc.)
            isShrinkResources = true

            // ProGuard rules to preserve Room and Koin reflection
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Link signing configuration only if keystore is available
            val keystorePath = System.getenv("KEYSTORE_PATH")
                ?: project.findProperty("keystorePath") as String?
            if (keystorePath != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

