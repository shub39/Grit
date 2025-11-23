@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.composeHotReload)
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.10.1"
}

kotlin {
    wasmJs {
        browser()
        binaries.executable()
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:core"))

            implementation(libs.material3)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.compose.windowSizeClass)

            implementation(libs.materialKolor)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-client:0.10.1")
            implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-server:0.10.1")
            implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:0.10.1")
            implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-client:0.10.1")
            implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-server:0.10.1")
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}