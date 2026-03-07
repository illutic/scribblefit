plugins {
    id("scribblefit.android.feature")
}

android {
    namespace = "com.scribblefit.feature.canvas"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":feature:ai:domain"))
}
