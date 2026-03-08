plugins {
    id("scribblefit.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.feature.analytics.domain"
}

dependencies {
    implementation(project(":feature:ai:domain"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines.core)
}
