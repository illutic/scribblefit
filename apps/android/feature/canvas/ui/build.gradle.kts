plugins {
    id("scribblefit.android.library")
    id("scribblefit.android.library.compose")
}

android {
    namespace = "com.scribblefit.feature.canvas.ui"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":feature:canvas:domain"))
}
