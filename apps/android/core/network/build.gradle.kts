plugins {
    id("scribblefit.android.library.compose")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.core.network"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
