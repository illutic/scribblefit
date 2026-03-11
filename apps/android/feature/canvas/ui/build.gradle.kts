plugins {
    id("scribblefit.android.feature")
}

android {
    namespace = "com.scribblefit.feature.canvas.ui"
}

dependencies {
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:scribble:domain"))
    implementation(project(":feature:workout:domain"))
    implementation(project(":feature:canvas:domain"))
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.hilt.navigation.compose)
}
