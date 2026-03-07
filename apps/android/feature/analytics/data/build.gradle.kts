plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.feature.analytics.data"
}

dependencies {
    implementation(project(":feature:analytics:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:ai:data"))
    implementation(project(":core:database"))

    implementation(libs.hilt.android)
    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
}
