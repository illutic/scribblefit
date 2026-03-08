plugins {
    id("scribblefit.android.library.compose")
}

android {
    namespace = "com.scribblefit.core.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.material.icons)
}
