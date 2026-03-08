plugins {
    id("scribblefit.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.feature.profile.domain"
}

dependencies {
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:ledger"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines.core)
}
