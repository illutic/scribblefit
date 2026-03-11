plugins {
    id("scribblefit.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.feature.canvas.domain"
}

dependencies {
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:scribble:domain"))
    implementation(project(":feature:workout:domain"))
    implementation(project(":feature:ledger"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines.core)
}
