plugins {
    id("scribblefit.android.feature")
}

android {
    namespace = "com.scribblefit.feature.canvas"
}

dependencies {
    implementation(project(":feature:canvas:domain"))
    implementation(project(":feature:ai:domain"))
    implementation(project(":core:designsystem"))
}
