plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.compose.compiler)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

android {
    namespace = "dev.sanson.donezo.backend.git"

    compileSdkVersion(libs.versions.compilesdk.get().toInt())

    defaultConfig {
        minSdk = libs.versions.minsdk.get().toInt()
    }

    buildFeatures {
        compose = true
        resValues = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":backend"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    implementation(libs.kotlinx.coroutines.core)
}