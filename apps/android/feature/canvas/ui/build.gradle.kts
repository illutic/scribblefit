plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.library.compose")
    id("scribblefit.android.hilt")
}

android {
    namespace = "com.scribblefit.feature.canvas.ui"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:navigation"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:canvas:domain"))
    implementation(project(":feature:scribble:domain"))

    implementation(libs.androidx.material.icons)
}
