plugins {
    id("scribblefit.android.library")
}

android {
    namespace = "com.scribblefit.feature.canvas.domain"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":feature:scribble:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(libs.slf4j.android)
}
