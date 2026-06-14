plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.performance) apply false
}

allprojects {
    configurations.all {
        resolutionStrategy {
            val kotlinVersion = libs.versions.kotlin.get()
            force("org.jetbrains.kotlin:kotlin-metadata-jvm:$kotlinVersion")
        }
    }
}
