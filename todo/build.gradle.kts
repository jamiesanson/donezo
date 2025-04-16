plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "dev.sanson.donezo.todo"

    compileSdkVersion(libs.versions.compilesdk.get().toInt())

    defaultConfig {
        minSdk = libs.versions.minsdk.get().toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    api(project(":model"))
    api(project(":arch"))
    api(project(":backend"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.bundles.unittesting)
}