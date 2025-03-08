import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

val appName = "Grit"
val gitHash = execute("git", "rev-parse", "HEAD").take(7)

android {
    namespace = "com.shub39.grit"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shub39.grit"
        minSdk = 29
        targetSdk = 35
        versionCode = 160
        versionName = "1.6.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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

        create("beta"){
            resValue("string", "app_name", "$appName Beta")
            applicationIdSuffix = ".beta"
            isMinifyEnabled = true
            isShrinkResources = true
            versionNameSuffix = "-beta-$gitHash"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            resValue("string", "app_name", "$appName Debug")
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
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

dependencies {

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.koin.androidx.compose)
    implementation(libs.reorderable)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.sqlite.bundled)
    implementation(libs.materialKolor)
    implementation(libs.colorpicker.compose)
    implementation(libs.androidx.datastore.preferences.core)

}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

fun execute(vararg command: String): String {
    val outputStream = ByteArrayOutputStream()
    project.exec {
        commandLine(*command)
        standardOutput = outputStream
    }
    return outputStream.toString().trim()
}