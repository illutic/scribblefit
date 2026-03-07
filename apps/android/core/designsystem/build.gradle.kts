plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.library.compose")
}

android {
    namespace = "com.scribblefit.core.designsystem"
}

dependencies {
    api(libs.androidx.material3)
    api(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
}
