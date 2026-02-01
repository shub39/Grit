@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    jvm()

    androidLibrary {
        namespace = "com.shub39.grit.core"
        compileSdk = libs.versions.compileSdk.get().toInt()
        androidResources.enable = true
    }

    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.navigation.compose)
            implementation(libs.compose.windowsizeclass)

            implementation(libs.kotlinx.datetime)
            implementation(libs.reorderable)
            implementation(libs.compose.charts)
            implementation(libs.calendar)
            implementation(libs.materialkolor)
        }
    }
}

compose.resources {
    publicResClass = true
}