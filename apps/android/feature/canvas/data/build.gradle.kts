plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.canvas.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":feature:canvas:domain"))
}
