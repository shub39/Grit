plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.compose.multiplatform)
}

val appName = "Grit"
val appVersionCode = 5610
val appVersionName = "5.6.1"

val gitHash = execute("git", "rev-parse", "HEAD").take(7)

android {
    namespace = "com.shub39.grit"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.shub39.grit"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = appVersionCode
        versionName = appVersionName

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
//            isMinifyEnabled = true
//            isShrinkResources = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
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
    implementation(projects.shared.core)

    "playImplementation"(libs.purchases)
    "playImplementation"(libs.purchases.ui)

    implementation(libs.compose.material3)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.components.resources)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling.preview)

    implementation(libs.navigation.compose)
    implementation(libs.compose.windowsizeclass)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.androidx.glance.appwidget.preview)
    implementation(libs.androidx.glance.preview)
    implementation(libs.materialkolor)
    implementation(libs.colorpicker.compose)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.biometric)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.room.testing)
    testImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.truth)

    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.navigation)
    ksp(libs.koin.ksp.compiler)
    api(libs.koin.annotations)
}

room {
    schemaDirectory("$projectDir/schemas")
}

fun execute(vararg command: String): String = providers.exec {
    commandLine(*command)
}.standardOutput.asText.get().trim()

val generateChangelogJson by tasks.registering {
    val inputFile = rootProject.file("CHANGELOG.md")
    val outputDir = file("$projectDir/src/main/assets/")
    val outputFile = File(outputDir, "changelog.json")

    inputs.file(inputFile)
    outputs.file(outputFile)

    doLast {
        if (!outputDir.exists()) outputDir.mkdirs()

        val lines = inputFile.readLines()

        val map = mutableMapOf<String, MutableList<String>>()
        var currentVersion: String? = null

        for (line in lines) {
            when {
                line.startsWith("## ") -> {
                    currentVersion = line.removePrefix("## ").trim()
                    map[currentVersion] = mutableListOf()
                }

                line.startsWith("- ") && currentVersion != null -> {
                    map[currentVersion]?.add(
                        line.removePrefix("- ").trim()
                    )
                }
            }
        }

        val json = buildString {
            append("[\n")

            map.entries.forEachIndexed { index, entry ->
                append("  {\n")
                append("    \"version\": \"${entry.key}\",\n")
                append("    \"changes\": [\n")

                entry.value.forEachIndexed { i, item ->
                    append("      \"${item.replace("\"", "\\\"")}\"")
                    if (i != entry.value.lastIndex) append(",")
                    append("\n")
                }

                append("    ]\n")
                append("  }")

                if (index != map.entries.size - 1) append(",")
                append("\n")
            }

            append("]")
        }


        outputFile.writeText(json)
    }
}

tasks.named("preBuild") {
    dependsOn(generateChangelogJson)
}