/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.koin.compiler)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-sensitive-resolution")
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
        optIn.add(
            "androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi"
        )
    }

    jvm()

    android {
        namespace = "com.shub39.grit.shared.ui"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        androidResources { enable = true }
    }

    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)

            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.windowsizeclass)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.jetbrains.navigation3.ui)

            implementation(libs.kotlinx.datetime)
            implementation(libs.reorderable)
            implementation(libs.calendar)
            implementation(libs.materialkolor)
            implementation(libs.colorpicker.compose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.koin.annotations)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.ui.tooling)
    androidRuntimeClasspath(libs.compose.ui.tooling.preview)
}

androidComponents {
    onVariants { variant ->
        variant.sources.res?.addStaticSourceDirectory("src/commonMain/composeResources")
    }
}
