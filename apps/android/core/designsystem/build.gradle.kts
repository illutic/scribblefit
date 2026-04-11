plugins {
    id("scribblefit.android.library.compose")
}

android {
    namespace = "com.scribblefit.core.designsystem"
}

dependencies {
    implementation(project(":core:navigation"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui.text.google.fonts)
    api(libs.androidx.material.icons)
}
