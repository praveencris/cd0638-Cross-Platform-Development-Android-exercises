import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
        versionCode = 1
        versionName = "1.0"
        // TODO (Bonus): Centralize version management using gradle.properties
        // Define APP_VERSION_MAJOR, APP_VERSION_MINOR, APP_VERSION_PATCH in gradle.properties
        // Then read them here: val major = project.property("APP_VERSION_MAJOR").toString().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // TODO 1: Create a signingConfigs block for release builds
    // - Create a "release" signing configuration
    // - Reference a keystore file (use System.getenv("KEYSTORE_PATH") or project.findProperty("keystorePath"))
    // - Include storeFile, storePassword, keyAlias, and keyPassword
    // - Use environment variables or local.properties to keep credentials out of version control
    // Example structure:
    // signingConfigs {
    //     create("release") {
    //         storeFile = file("path/to/release.keystore")
    //         storePassword = "your-store-password"
    //         keyAlias = "your-key-alias"
    //         keyPassword = "your-key-password"
    //     }
    // }

    buildTypes {
        getByName("release") {
            // TODO 2: Enable R8 code shrinking and resource optimization
            // - Set isMinifyEnabled = true to enable R8 code shrinking
            // - Set isShrinkResources = true to remove unused resources
            // - Add proguardFiles() to link ProGuard rules:
            //   proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            // TODO 3: Connect the signing configuration to the release build type
            // - Set signingConfig = signingConfigs.getByName("release")
            // - Make it conditional: only apply if keystore path is provided
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

