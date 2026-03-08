plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.core.navigation"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.navigation.compose)
}
