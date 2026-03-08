plugins {
    id("scribblefit.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.feature.ai.domain"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines.core)
}
