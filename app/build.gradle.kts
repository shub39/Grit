import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.room)
}

val appName = "Grit"
val appVersionCode = 5100
val appVersionName = "5.1.0"

val gitHash = execute("git", "rev-parse", "HEAD").take(7)

android {
    namespace = "com.shub39.grit"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.shub39.grit"
        minSdk = 29
        targetSdk = 36
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    sourceSets {
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

    kotlin {
        compilerOptions {
            jvmToolchain(17)
        }
    }

    buildTypes {
        release {
            resValue("string", "app_name", appName)
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("beta") {
            resValue("string", "app_name", "$appName Beta")
            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta$gitHash"
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "$appName Debug")
        }
    }

    flavorDimensions += "version"

    productFlavors {
        create("play") {
            dimension = "version"
            versionNameSuffix = "-play"
        }
        create("foss") {
            dimension = "version"
        }
    }

    applicationVariants.all {
        outputs.all {
            val apkOutput = this as ApkVariantOutputImpl
            apkOutput.outputFileName = "app-release.apk"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

aboutLibraries {
    export.excludeFields.add("generated")
    library {
        duplicationMode = DuplicateMode.MERGE
        duplicationRule = DuplicateRule.SIMPLE
    }
}

dependencies {
    "playImplementation"(libs.purchases)
    "playImplementation"(libs.purchases.ui)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.koin.androidx.compose)
    implementation(libs.reorderable)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.materialKolor)
    implementation(libs.colorpicker.compose)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.aboutLibraries)
    implementation(libs.composeIcons.fontAwesome)
    implementation(libs.compose.charts)
    implementation(libs.calendar)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.androidx.biometric)
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.room.testing)
    testImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.truth)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
}

room {
    schemaDirectory("$projectDir/schemas")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

fun execute(vararg command: String): String = providers.exec {
    commandLine(*command)
}.standardOutput.asText.get().trim()