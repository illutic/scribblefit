plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.canvas.domain"
}

dependencies {
    implementation(project(":core:ai"))
    implementation(project(":core:model"))
}
