plugins {
    id("scribblefit.android.feature")
}

android {
    namespace = "com.scribblefit.feature.canvas.ui"
}

dependencies {
    implementation(project(":feature:ai:domain"))
    implementation(project(":feature:canvas:domain"))
    implementation(project(":feature:canvas:data"))
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.hilt.navigation.compose)
}
