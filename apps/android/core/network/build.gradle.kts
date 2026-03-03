plugins {
    id("scribblefit.android.library.compose")
    id("scribblefit.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.scribblefit.core.network"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
}
