pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    rulesMode = RulesMode.FAIL_ON_PROJECT_RULES
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Donezo"

include(":android")
include(":todo")
include(":model")
include(":arch")
include(":backend")
include(":git")