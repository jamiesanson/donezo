import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    alias(libs.plugins.compose.compiler)
}

android {
    compileSdkVersion(libs.versions.compilesdk.get().toInt())

    namespace = "dev.sanson.donezo"

    defaultConfig {
        applicationId = "dev.sanson.donezo"
        minSdk = libs.versions.minsdk.get().toInt()
        targetSdk = libs.versions.targetsdk.get().toInt()

        versionCode = libs.versions.versioncode.get().toInt()
        versionName = libs.versions.app.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            // Allow local, unsigned release builds
            if (!rootProject.file("keystore/").exists()) {
                return@create
            }

            val keystorePropertiesFile = rootProject.file("keystore.properties")
            val keystoreProperties = Properties()

            keystoreProperties.load(FileInputStream(keystorePropertiesFile))

            storeFile = rootProject.file("keystore/keystore.jks")
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }

        getByName("debug") {
            storeFile = rootProject.file("debug.keystore")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            if (rootProject.file("keystore/").exists()) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = signingConfigs.getByName("debug")
            }
        }

        getByName("debug") {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/*.kotlin_module",
                "plugin.properties"
            )
        }
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core)
    implementation(libs.bundles.lifecycle)

    implementation(libs.bundles.compose)
    implementation(libs.androidx.activity.compose)

    implementation(libs.material)

    implementation(libs.datastore)

    implementation(project(":todo"))
    implementation(project(":git"))

    coreLibraryDesugaring(libs.corelibrarydesugaring)
}