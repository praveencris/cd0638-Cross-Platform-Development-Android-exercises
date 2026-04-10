import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    kotlin("native.cocoapods")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosArm64()
    iosSimulatorArm64()

    // Configure CocoaPods for Firebase iOS dependencies
    cocoapods {
        summary = "Recall Shared Module"
        homepage = "https://github.com/udacity/recall"
        version = "1.0"
        ios.deploymentTarget = "15.0"

        // Static framework ensures Firebase static xcframeworks are linked
        // directly into shared.framework — no dynamic loading at runtime
        framework {
            baseName = "shared"
            isStatic = true
        }

        // Firebase pods for iOS
        pod("FirebaseAnalytics") {
            version = "~> 11.8.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseCrashlytics") {
            version = "~> 11.8.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseRemoteConfig") {
            version = "~> 11.8.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }

    // Workaround for duplicate Kotlin Native libraries in ~/.konan and /var/root/.konan
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xklib-duplicated-unique-name-strategy=allow-first-with-warning")
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)

            // Firebase dependencies for Android
            // Using explicit versions since platform() is deprecated in KMP with Kotlin 2.3+
            // These versions match Firebase BOM 33.7.0 defined in libs.versions.toml
            implementation("com.google.firebase:firebase-analytics:22.1.2")
            implementation("com.google.firebase:firebase-crashlytics:19.2.1")
            implementation("com.google.firebase:firebase-config:22.0.1")
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

android {
    namespace = "com.recall.app.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}
