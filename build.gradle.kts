plugins {
    alias(libs.plugins.spotless)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
}

spotless {
    format("misc") {
        target("**/*.gradle", "**/*.md", "**/.gitignore")

        leadingTabsToSpaces()
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlin {
        target("**/*.kt")

        ktlint(libs.versions.ktlint.get())
    }
}
