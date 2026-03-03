plugins {
    id("scribblefit.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.core.ai"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
