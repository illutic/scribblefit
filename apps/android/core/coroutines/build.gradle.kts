plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.core.coroutines"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    api(libs.coroutines.core)
    api(libs.coroutines.android)
}
