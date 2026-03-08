plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.feature.analytics.data"
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:analytics:domain"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines.core)
}
