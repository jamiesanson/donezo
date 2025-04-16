plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "dev.sanson.donezo.backend"

    compileSdkVersion(libs.versions.compilesdk.get().toInt())

    defaultConfig {
        minSdk = libs.versions.minsdk.get().toInt()
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    api(project(":model"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.kotlinx.coroutines.core)

    coreLibraryDesugaring(libs.corelibrarydesugaring)
}