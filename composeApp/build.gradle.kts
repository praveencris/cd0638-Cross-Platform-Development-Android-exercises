import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
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

        // Version from gradle.properties (Single Source of Truth)
        val major = (project.findProperty("APP_VERSION_MAJOR") as String).toInt()
        val minor = (project.findProperty("APP_VERSION_MINOR") as String).toInt()
        val patch = (project.findProperty("APP_VERSION_PATCH") as String).toInt()
        versionCode = major * 1_000_000 + minor * 1_000 + patch
        versionName = "$major.$minor.$patch"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // Signing configuration for release builds
    // Credentials loaded from keystore.properties (Google's recommended approach)
    // Falls back to environment variables for CI/CD pipelines
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties()
    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    }

    signingConfigs {
        create("release") {
            val ksPath = keystoreProperties["storeFile"] as String?
                ?: System.getenv("KEYSTORE_PATH")

            if (ksPath != null) {
                storeFile = file(ksPath)
                storePassword = keystoreProperties["storePassword"] as String?
                    ?: System.getenv("KEYSTORE_PASSWORD")
                keyAlias = keystoreProperties["keyAlias"] as String?
                    ?: System.getenv("KEY_ALIAS")
                keyPassword = keystoreProperties["keyPassword"] as String?
                    ?: System.getenv("KEY_PASSWORD")
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
            val ksPath = keystoreProperties["storeFile"] as String?
                ?: System.getenv("KEYSTORE_PATH")
            if (ksPath != null) {
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

